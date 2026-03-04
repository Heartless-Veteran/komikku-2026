package eu.kanade.tachiyomi.ui.search

import androidx.compose.runtime.Immutable
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import eu.kanade.domain.search.SearchHistoryRepository
import eu.kanade.tachiyomi.source.CatalogueSource
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import mihon.domain.manga.model.toDomainManga
import tachiyomi.core.common.util.lang.launchIO
import tachiyomi.domain.manga.interactor.GetLibraryManga
import tachiyomi.domain.manga.interactor.NetworkToLocalManga
import tachiyomi.domain.manga.model.Manga
import tachiyomi.domain.source.model.Source
import tachiyomi.domain.source.service.SourceManager
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

/**
 * ScreenModel for the Universal Search screen that searches across
 * library, history, and catalogue sources simultaneously.
 */
class UniversalSearchScreenModel(
    private val getLibraryManga: GetLibraryManga = Injekt.get(),
    private val sourceManager: SourceManager = Injekt.get(),
    private val networkToLocalManga: NetworkToLocalManga = Injekt.get(),
    private val searchHistoryRepository: SearchHistoryRepository = Injekt.get(),
) : StateScreenModel<UniversalSearchScreenModel.State>(State()) {

    private var searchJob: Job? = null

    fun updateQuery(query: String) {
        mutableState.update { it.copy(query = query) }
    }

    fun search(query: String = state.value.query) {
        if (query.isBlank()) return

        updateQuery(query)
        mutableState.update { it.copy(isLoading = true, sourceResults = emptyMap()) }

        searchJob?.cancel()
        searchJob = screenModelScope.launchIO {
            // Search library
            val library = getLibraryManga.await()
            val libraryResults = library
                .filter { it.manga.title.contains(query, ignoreCase = true) }
                .map { it.manga }

            // History: find manga in library that were recently read and match query
            val historyResults = library
                .filter { it.lastRead > 0 && it.manga.title.contains(query, ignoreCase = true) }
                .sortedByDescending { it.lastRead }
                .map { it.manga }
                .distinctBy { it.id }

            mutableState.update {
                it.copy(
                    libraryResults = libraryResults,
                    historyResults = historyResults,
                )
            }

            // Search sources in parallel (limit to first 5 for performance)
            val enabledSources = sourceManager.getVisibleCatalogueSources()
                .filterIsInstance<CatalogueSource>()
                .take(5)

            val sourceResultMap = mutableMapOf<Source, List<Manga>>()

            enabledSources.map { source ->
                async {
                    try {
                        val page = source.getSearchManga(1, query, source.getFilterList())
                        val domainManga = page.mangas
                            .map { sm -> sm.toDomainManga(source.id) }
                            .distinctBy { it.url }
                            .let { networkToLocalManga(it) }

                        val domainSource = Source(
                            id = source.id,
                            lang = source.lang,
                            name = source.name,
                            supportsLatest = source.supportsLatest,
                            isStub = false,
                        )
                        synchronized(sourceResultMap) {
                            sourceResultMap[domainSource] = domainManga
                        }
                    } catch (_: Exception) {
                        // Ignore individual source failures
                    }
                }
            }.awaitAll()

            mutableState.update {
                it.copy(
                    sourceResults = sourceResultMap,
                    isLoading = false,
                )
            }

            // Save to search history
            searchHistoryRepository.addSearch(query)
        }
    }

    @Immutable
    data class State(
        val query: String = "",
        val isLoading: Boolean = false,
        val libraryResults: List<Manga> = emptyList(),
        val historyResults: List<Manga> = emptyList(),
        val sourceResults: Map<Source, List<Manga>> = emptyMap(),
    )
}
