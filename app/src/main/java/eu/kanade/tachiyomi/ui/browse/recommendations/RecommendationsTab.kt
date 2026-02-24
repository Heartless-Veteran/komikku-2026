package eu.kanade.tachiyomi.ui.browse.recommendations

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Recommend
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.TabOptions
import eu.kanade.presentation.browse.recommendations.RecommendationsScreenContent
import eu.kanade.presentation.util.Tab
import eu.kanade.tachiyomi.R
import eu.kanade.tachiyomi.ui.main.MainActivity
import eu.kanade.tachiyomi.ui.manga.MangaScreen
import kotlinx.collections.immutable.persistentListOf
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.i18n.stringResource
import tachiyomi.presentation.core.util.plus

/**
 * Tab for displaying AI-powered manga recommendations.
 */
class RecommendationsTab(
    private val fromSource: Boolean = false,
) : Tab {

    override val options: TabOptions
        @Composable
        get() {
            val isSelected = LocalTabNavigator.current.current.key == key
            return TabOptions(
                index = 0u,
                title = stringResource(MR.strings.browse_recommendations),
                icon = null,
            )
        }

    @Composable
    override fun Content() {
        val context = LocalContext.current
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { RecommendationsScreenModel() }
        val state by screenModel.state.collectAsState()

        LaunchedEffect(Unit) {
            (context as? MainActivity)?.ready = true
        }

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
    }
}

/**
 * Factory function to create the Recommendations tab.
 */
fun recommendationsTab(fromSource: Boolean = false): RecommendationsTab {
    return RecommendationsTab(fromSource)
}
