package eu.kanade.tachiyomi.ui.reader.history

import eu.kanade.tachiyomi.ui.reader.model.ReaderChapter
import tachiyomi.domain.chapter.model.Chapter
import tachiyomi.domain.history.interactor.UpsertHistory
import tachiyomi.domain.history.model.HistoryUpdate
import tachiyomi.domain.track.interactor.TrackChapter
import java.util.Date

/**
 * Tracks reading history for AI recommendations.
 * Extracted from ReaderViewModel for better separation of concerns.
 */
class ReadingHistoryTracker(
    private val upsertHistory: UpsertHistory,
    private val trackChapter: TrackChapter,
) {
    private val startTimes = mutableMapOf<Long, Long>() // chapterId -> startTime
    
    /**
     * Called when user starts reading a chapter.
     */
    suspend fun startReading(chapter: Chapter) {
        startTimes[chapter.id!!] = System.currentTimeMillis()
    }
    
    /**
     * Called when user stops reading (switches chapter or exits reader).
     */
    suspend fun stopReading(
        chapter: Chapter,
        mangaId: Long,
        pagesRead: Int,
        totalPages: Int,
    ) {
        val startTime = startTimes.remove(chapter.id!!) ?: return
        val timeSpent = System.currentTimeMillis() - startTime
        
        // Update history in database
        upsertHistory.await(
            HistoryUpdate(
                chapterId = chapter.id,
                readAt = Date(),
                readDuration = timeSpent,
            )
        )
        
        // Track with tracking services (AniList, MAL, etc.)
        if (pagesRead > 0) {
            trackChapter.await(mangaId, chapter.chapterNumber)
        }
    }
    
    /**
     * Mark chapter as completely read.
     */
    suspend fun markChapterRead(
        chapter: Chapter,
        mangaId: Long,
    ) {
        upsertHistory.await(
            HistoryUpdate(
                chapterId = chapter.id!!,
                readAt = Date(),
                readDuration = startTimes.remove(chapter.id) ?: 0L,
            )
        )
        trackChapter.await(mangaId, chapter.chapterNumber)
    }
    
    /**
     * Get reading time for a chapter (in milliseconds).
     */
    fun getCurrentReadingTime(chapterId: Long): Long {
        val startTime = startTimes[chapterId] ?: return 0L
        return System.currentTimeMillis() - startTime
    }
}