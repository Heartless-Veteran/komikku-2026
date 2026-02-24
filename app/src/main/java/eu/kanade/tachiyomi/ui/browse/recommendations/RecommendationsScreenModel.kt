package eu.kanade.tachiyomi.ui.browse.recommendations

import androidx.compose.runtime.Immutable
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mihon.domain.recommendation.interactor.GetBecauseYouReadRecommendations
import mihon.domain.recommendation.interactor.GetRecommendations
import mihon.domain.recommendation.model.BecauseYouReadRecommendation
import mihon.domain.recommendation.model.Recommendation
import tachiyomi.core.common.util.system.logcat
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class RecommendationsScreenModel(
    private val getRecommendations: GetRecommendations = Injekt.get(),
    private val getBecauseYouReadRecommendations: GetBecauseYouReadRecommendations = Injekt.get(),
) : StateScreenModel<RecommendationsScreenModel.State>(State()) {

    init {
        screenModelScope.launch {
            loadRecommendations()
        }
    }

    private suspend fun loadRecommendations() {
        mutableState.update { it.copy(isLoading = true) }
        
        try {
            // Load "For You" recommendations
            val forYouRecommendations = getRecommendations.await(limit = 10)
            
            // Load "Because you read" recommendations
            val becauseYouRead = getBecauseYouReadRecommendations.await(limitPerSource = 5)
            
            mutableState.update {
                it.copy(
                    forYouRecommendations = forYouRecommendations.toImmutableList(),
                    becauseYouReadRecommendations = becauseYouRead.toImmutableList(),
                    isLoading = false,
                )
            }
        } catch (e: Exception) {
            logcat { "Error loading recommendations: ${e.message}" }
            mutableState.update {
                it.copy(
                    isLoading = false,
                    error = e.message,
                )
            }
        }
    }

    fun refresh() {
        screenModelScope.launch {
            loadRecommendations()
        }
    }

    @Immutable
    data class State(
        val forYouRecommendations: ImmutableList<Recommendation> = persistentListOf(),
        val becauseYouReadRecommendations: ImmutableList<BecauseYouReadRecommendation> = persistentListOf(),
        val isLoading: Boolean = true,
        val error: String? = null,
    )
}
