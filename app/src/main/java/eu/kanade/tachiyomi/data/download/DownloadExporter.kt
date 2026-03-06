package eu.kanade.tachiyomi.data.download

import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import com.hippo.unifile.UniFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import logcat.LogPriority
import tachiyomi.core.common.util.system.logcat
import tachiyomi.domain.chapter.model.Chapter
import tachiyomi.domain.manga.model.Manga
import tachiyomi.domain.source.service.SourceManager
import java.io.BufferedOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

/**
 * Exports downloaded chapters to CBZ (Comic Book ZIP) format.
 *
 * Uses [DownloadProvider] to locate the already-downloaded chapter files and
 * [ContentResolver] to write to Android Storage Access Framework (SAF) URIs.
 */
// KMK -->
class DownloadExporter(
    private val context: Context,
    private val downloadProvider: DownloadProvider,
    private val sourceManager: SourceManager,
) {
    /**
     * Exports a single downloaded chapter to a CBZ file at the given output URI.
     *
     * The caller is responsible for creating the output document URI via SAF
     * (e.g. [DocumentsContract.createDocument]) before calling this method.
     *
     * @param manga       the manga the chapter belongs to.
     * @param chapter     the chapter to export.
     * @param outputUri   the content URI of the target CBZ file to write into.
     * @return [Flow] emitting [ExportProgress] with percent 0-100 and the final URI on success,
     *         or percent -1 and an error message on failure.
     */
    fun exportChapter(
        manga: Manga,
        chapter: Chapter,
        outputUri: Uri,
    ): Flow<ExportProgress> = flow {
        emit(ExportProgress(0, null))

        try {
            val source = sourceManager.getOrStub(manga.source)
            val chapterDir = downloadProvider.findChapterDir(
                chapter.name,
                chapter.scanlator,
                chapter.url,
                manga.ogTitle,
                source,
            )

            if (chapterDir == null) {
                emit(ExportProgress(-1, null, "Chapter not downloaded"))
                return@flow
            }

            context.contentResolver.openOutputStream(outputUri)?.use { outputStream ->
                if (chapterDir.isFile) {
                    // Already stored as a CBZ – copy it directly.
                    chapterDir.openInputStream().use { it.copyTo(outputStream) }
                } else {
                    // Stored as a directory of images – build a CBZ on-the-fly.
                    val pageFiles = chapterDir.listFiles().orEmpty()
                        .filter { it.isFile && it.name != null }
                        .sortedBy { it.name }

                    if (pageFiles.isEmpty()) {
                        emit(ExportProgress(-1, null, "No page files found for chapter"))
                        return@flow
                    }

                    ZipOutputStream(BufferedOutputStream(outputStream)).use { zipOut ->
                        pageFiles.forEachIndexed { index, pageFile ->
                            val entryName = pageFile.name ?: String.format("%04d.jpg", index + 1)
                            zipOut.putNextEntry(ZipEntry(entryName))
                            pageFile.openInputStream().use { it.copyTo(zipOut) }
                            zipOut.closeEntry()
                            emit(ExportProgress((index + 1) * 100 / pageFiles.size, null))
                        }
                    }
                }
            } ?: run {
                emit(ExportProgress(-1, null, "Failed to open output stream"))
                return@flow
            }

            emit(ExportProgress(100, outputUri))
        } catch (e: Exception) {
            logcat(LogPriority.ERROR, e) { "Failed to export chapter '${chapter.name}'" }
            emit(ExportProgress(-1, null, e.message))
        }
    }.flowOn(Dispatchers.IO)

    /**
     * Exports all downloaded chapters of a manga into [outputDirUri] (a SAF tree URI).
     *
     * Each chapter becomes a separate CBZ file named
     * `"<manga title> - <chapter name>.cbz"` inside [outputDirUri].
     *
     * @return [Flow] emitting [MangaExportProgress] with overall progress and the list of
     *         created URIs on completion, or an error message on failure.
     */
    fun exportManga(
        manga: Manga,
        chapters: List<Chapter>,
        outputDirUri: Uri,
    ): Flow<MangaExportProgress> = flow {
        emit(MangaExportProgress(0, 0, chapters.size, null))

        val exportedUris = mutableListOf<Uri>()
        val mangaTitle = manga.title.sanitizeForFilename()

        chapters.forEachIndexed { chapterIndex, chapter ->
            val chapterName = chapter.name.sanitizeForFilename()
            val filename = "$mangaTitle - $chapterName.cbz"

            val chapterFileUri = DocumentsContract.createDocument(
                context.contentResolver,
                outputDirUri,
                "application/x-cbz",
                filename,
            )

            if (chapterFileUri == null) {
                emit(
                    MangaExportProgress(
                        overallPercent = chapterIndex * 100 / chapters.size,
                        currentChapter = chapterIndex + 1,
                        totalChapters = chapters.size,
                        exportedUris = null,
                        error = "Could not create output file for '${chapter.name}'",
                    ),
                )
                return@flow
            }

            exportChapter(manga, chapter, chapterFileUri).collect { progress ->
                val overallPercent = (chapterIndex * 100 + progress.percent) / chapters.size
                when {
                    progress.percent < 0 -> {
                        emit(
                            MangaExportProgress(
                                overallPercent = overallPercent,
                                currentChapter = chapterIndex + 1,
                                totalChapters = chapters.size,
                                exportedUris = null,
                                error = progress.error,
                            ),
                        )
                        return@collect
                    }
                    progress.uri != null -> exportedUris.add(progress.uri)
                    else -> emit(MangaExportProgress(overallPercent, chapterIndex + 1, chapters.size, null))
                }
            }
        }

        emit(MangaExportProgress(100, chapters.size, chapters.size, exportedUris))
    }.flowOn(Dispatchers.IO)

    private fun String.sanitizeForFilename(): String {
        return this.replace(Regex("[\\\\/:*?\"<>|\\x00-\\x1F]"), "_")
            .trim()
            .take(100)
    }

    data class ExportProgress(
        val percent: Int,
        val uri: Uri?,
        val error: String? = null,
    )

    data class MangaExportProgress(
        val overallPercent: Int,
        val currentChapter: Int,
        val totalChapters: Int,
        val exportedUris: List<Uri>?,
        val error: String? = null,
    )
}
// KMK <--
