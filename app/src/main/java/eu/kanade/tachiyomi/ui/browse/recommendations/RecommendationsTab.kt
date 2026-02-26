package eu.kanade.tachiyomi.ui.browse.recommendations

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import eu.kanade.presentation.browse.recommendations.RecommendationsScreenContent
import eu.kanade.presentation.components.TabContent
import eu.kanade.tachiyomi.ui.manga.MangaScreen
import tachiyomi.i18n.MR

@Composable
fun Screen.recommendationsTab(): TabContent {
    val navigator = LocalNavigator.currentOrThrow
    val screenModel = rememberScreenModel { RecommendationsScreenModel() }
    val state by screenModel.state.collectAsState()

    return TabContent(
        titleRes = MR.strings.browse_recommendations,
        content = { _: PaddingValues, _ ->
            RecommendationsScreenContent(
                forYouRecommendations = state.forYouRecommendations,
                becauseYouReadRecommendations = state.becauseYouReadRecommendations,
                isLoading = state.isLoading,
                error = state.error,
                onRefresh = screenModel::refresh,
                onMangaClick = { mangaId ->
                    navigator.push(MangaScreen(mangaId))
                },
            )
        },
    )
}
