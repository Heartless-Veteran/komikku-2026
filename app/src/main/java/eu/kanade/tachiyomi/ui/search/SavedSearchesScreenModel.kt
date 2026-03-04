package eu.kanade.tachiyomi.ui.search

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import eu.kanade.domain.search.SavedSearchRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import tachiyomi.core.common.util.lang.launchIO
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

/**
 * ScreenModel for the Saved Searches screen.
 */
class SavedSearchesScreenModel(
    private val savedSearchRepository: SavedSearchRepository = Injekt.get(),
) : StateScreenModel<SavedSearchesScreenModel.State>(State()) {

    init {
        screenModelScope.launchIO {
            savedSearchRepository.getSavedSearches().collectLatest { searches ->
                mutableState.update { it.copy(savedSearches = searches) }
            }
        }
    }

    fun deleteSearch(id: String) {
        screenModelScope.launchIO {
            savedSearchRepository.deleteSearch(id)
        }
    }

    fun toggleNotification(id: String, enabled: Boolean) {
        screenModelScope.launchIO {
            savedSearchRepository.toggleNotifications(id, enabled)
        }
    }

    data class State(
        val savedSearches: List<SavedSearchRepository.SavedSearch> = emptyList(),
    )
}
