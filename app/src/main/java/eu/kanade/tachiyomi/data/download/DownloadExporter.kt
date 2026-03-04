package eu.kanade.tachiyomi.data.download

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import logcat.LogPriority
import tachiyomi.core.common.util.system.logcat
import tachiyomi.domain.chapter.model.Chapter
import tachiyomi.domain.manga.model.Manga
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

/**
 * Exports downloaded chapters to CBZ (Comic Book ZIP) format.
 */
class DownloadExporter(
    private val context: Context,
) {
    /**
     * Exports a chapter to CBZ format.
     * @return Flow of export progress (0-100) and final URI
     */
    fun exportChapter(
        manga: Manga,
        chapter: Chapter,
        pageFiles: List<File>,
        outputDir: Uri,
    ): Flow<ExportProgress> = flow {
        emit(ExportProgress(0, null))
        
        try {
            val mangaTitle = manga.title.sanitizeForFilename()
            val chapterName = chapter.name.sanitizeForFilename()
            val filename = "$mangaTitle - $chapterName.cbz"
            
            val outputFile = File(outputDir.path, filename)
            
            ZipOutputStream(BufferedOutputStream(FileOutputStream(outputFile))).use { zipOut ->
                pageFiles.forEachIndexed { index, pageFile ->
                    if (pageFile.exists()) {
                        val entryName = String.format("%04d.jpg", index + 1)
                        zipOut.putNextEntry(ZipEntry(entryName))
                        FileInputStream(pageFile).use { input ->
                            input.copyTo(zipOut)
                        }
                        zipOut.closeEntry()
                    }
                    emit(ExportProgress((index + 1) * 100 / pageFiles.size, null))
                }
            }
            
            emit(ExportProgress(100, Uri.fromFile(outputFile)))
        } catch (e: Exception) {
            logcat(LogPriority.ERROR, e) { "Failed to export chapter" }
            emit(ExportProgress(-1, null, e.message))
        }
    }.flowOn(Dispatchers.IO)

    /**
     * Exports all chapters of a manga as separate CBZ files.
     */
    fun exportManga(
        manga: Manga,
        chapters: List<Chapter>,
        outputDir: Uri,
    ): Flow<MangaExportProgress> = flow {
        emit(MangaExportProgress(0, 0, chapters.size, null))
        
        val exportedUris = mutableListOf<Uri>()
        
        chapters.forEachIndexed { chapterIndex, chapter ->
            // Get page files for this chapter
            val pageFiles = getPageFilesForChapter(manga, chapter)
            
            exportChapter(manga, chapter, pageFiles, outputDir).collect { progress ->
                when {
                    progress.percent >= 0 -> {
                        val overallPercent = (chapterIndex * 100 + progress.percent) / chapters.size
                        emit(MangaExportProgress(overallPercent, chapterIndex + 1, chapters.size, null))
                    }
                    progress.uri != null -> {
                        exportedUris.add(progress.uri)
                    }
                }
            }
        }
        
        emit(MangaExportProgress(100, chapters.size, chapters.size, exportedUris))
    }.flowOn(Dispatchers.IO)

    private fun getPageFilesForChapter(manga: Manga, chapter: Chapter): List<File> {
        // This would integrate with DownloadCache to get actual file paths
        // Placeholder implementation
        return emptyList()
    }

    private fun String.sanitizeForFilename(): String {
        return this.replace(Regex("[\\\\/:*?\"\u003c\u003e|]"), "_")
            .trim()
            .take(100) // Limit length
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
