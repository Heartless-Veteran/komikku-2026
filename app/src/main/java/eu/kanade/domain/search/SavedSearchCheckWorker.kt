package eu.kanade.domain.search

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import eu.kanade.tachiyomi.source.CatalogueSource
import eu.kanade.tachiyomi.source.SourceManager
import kotlinx.coroutines.flow.first
import tachiyomi.core.common.util.system.logcat
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

/**
 * Worker that checks saved searches for new results.
 * Runs weekly to notify users of new manga matching their saved searches.
 */
class SavedSearchCheckWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {

    private val savedSearchRepository: SavedSearchRepository = Injekt.get()
    private val sourceManager: SourceManager = Injekt.get()

    override suspend fun doWork(): Result {
        logcat { "Checking saved searches for new results" }

        val savedSearches = savedSearchRepository.getSavedSearches().first()
            .filter { it.notifyOnNewResults }

        var totalNewResults = 0

        savedSearches.forEach { search ->
            try {
                val currentCount = checkSearchResults(search)
                if (search.hasNewResults(currentCount)) {
                    val newCount = search.getNewResultCount(currentCount)
                    totalNewResults += newCount

                    // Show notification
                    showNewResultsNotification(search, newCount)

                    // Update last checked
                    savedSearchRepository.updateLastChecked(search.id, currentCount)
                }
            } catch (e: Exception) {
                logcat { "Error checking saved search ${search.id}: ${e.message}" }
            }
        }

        logcat { "Saved search check complete. Found $totalNewResults new results" }
        return Result.success()
    }

    private suspend fun checkSearchResults(search: SavedSearchRepository.SavedSearch): Int {
        // Get enabled sources
        val sources = sourceManager.getCatalogueSources()
            .filterIsInstance<CatalogueSource>()

        var totalResults = 0

        sources.forEach { source ->
            try {
                val page = source.getSearchManga(1, search.query, source.getFilterList())
                totalResults += page.mangas.size
            } catch (e: Exception) {
                // Ignore errors for individual sources
            }
        }

        return totalResults
    }

    private fun showNewResultsNotification(search: SavedSearchRepository.SavedSearch, newCount: Int) {
        // In production, this would create an actual Android notification
        logcat { "New results for '${search.query}': $newCount new manga found" }
    }

    companion object {
        const val WORK_NAME = "saved_search_check"
    }
}