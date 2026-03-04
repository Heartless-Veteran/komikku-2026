package eu.kanade.tachiyomi.ui.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import eu.kanade.presentation.search.UniversalSearchScreen
import eu.kanade.presentation.util.Screen
import eu.kanade.tachiyomi.ui.manga.MangaScreen

/**
 * Voyager screen for Universal Search — searches across library, history, and sources.
 */
class UniversalSearchScreen(
    private val initialQuery: String = "",
) : Screen() {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { UniversalSearchScreenModel() }
        val state by screenModel.state.collectAsState()

        if (initialQuery.isNotBlank()) {
            androidx.compose.runtime.LaunchedEffect(Unit) {
                screenModel.search(initialQuery)
            }
        }

        UniversalSearchScreen(
            query = state.query,
            onQueryChange = screenModel::updateQuery,
            onSearch = screenModel::search,
            libraryResults = state.libraryResults,
            historyResults = state.historyResults,
            sourceResults = state.sourceResults,
            isLoading = state.isLoading,
            onMangaClick = { manga -> navigator.push(MangaScreen(manga.id, true)) },
            onBackClick = navigator::pop,
        )
    }
}
