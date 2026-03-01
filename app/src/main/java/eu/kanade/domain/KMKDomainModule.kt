package eu.kanade.domain

import eu.kanade.domain.brightness.SmartBrightnessRepository
import eu.kanade.domain.readingstats.ReadingStatsRepository
import eu.kanade.domain.search.SavedSearchRepository
import eu.kanade.domain.search.SearchHistoryRepository
import eu.kanade.domain.search.SearchRankingRepository
import eu.kanade.domain.search.SearchSuggestionsRepository
import mihon.domain.recommendation.interactor.ClearOldRecommendations
import mihon.domain.recommendation.interactor.GetBecauseYouReadRecommendations
import mihon.domain.recommendation.interactor.GetReadingHistory
import mihon.domain.recommendation.interactor.GetRecommendations
import mihon.domain.recommendation.interactor.SyncMangaTags
import mihon.domain.recommendation.interactor.TrackReadingHistory
import mihon.domain.recommendation.interactor.UpdateRecommendationsCache
import mihon.domain.recommendation.repository.RecommendationsRepository
import tachiyomi.data.libraryUpdateError.LibraryUpdateErrorRepositoryImpl
import tachiyomi.data.libraryUpdateError.LibraryUpdateErrorWithRelationsRepositoryImpl
import tachiyomi.data.libraryUpdateErrorMessage.LibraryUpdateErrorMessageRepositoryImpl
import tachiyomi.data.recommendations.RecommendationsRepositoryImpl
import tachiyomi.domain.libraryUpdateError.interactor.DeleteLibraryUpdateErrors
import tachiyomi.domain.libraryUpdateError.interactor.GetLibraryUpdateErrorWithRelations
import tachiyomi.domain.libraryUpdateError.interactor.GetLibraryUpdateErrors
import tachiyomi.domain.libraryUpdateError.interactor.InsertLibraryUpdateErrors
import tachiyomi.domain.libraryUpdateError.repository.LibraryUpdateErrorRepository
import tachiyomi.domain.libraryUpdateError.repository.LibraryUpdateErrorWithRelationsRepository
import tachiyomi.domain.libraryUpdateErrorMessage.interactor.DeleteLibraryUpdateErrorMessages
import tachiyomi.domain.libraryUpdateErrorMessage.interactor.GetLibraryUpdateErrorMessages
import tachiyomi.domain.libraryUpdateErrorMessage.interactor.InsertLibraryUpdateErrorMessages
import tachiyomi.domain.libraryUpdateErrorMessage.repository.LibraryUpdateErrorMessageRepository
import uy.kohesive.injekt.api.InjektModule
import uy.kohesive.injekt.api.InjektRegistrar
import uy.kohesive.injekt.api.addFactory
import uy.kohesive.injekt.api.addSingletonFactory
import uy.kohesive.injekt.api.get

class KMKDomainModule : InjektModule {

    override fun InjektRegistrar.registerInjectables() {
        addSingletonFactory<LibraryUpdateErrorWithRelationsRepository> {
            LibraryUpdateErrorWithRelationsRepositoryImpl(get())
        }
        addFactory { GetLibraryUpdateErrorWithRelations(get()) }

        addSingletonFactory<LibraryUpdateErrorMessageRepository> { LibraryUpdateErrorMessageRepositoryImpl(get()) }
        addFactory { GetLibraryUpdateErrorMessages(get()) }
        addFactory { DeleteLibraryUpdateErrorMessages(get()) }
        addFactory { InsertLibraryUpdateErrorMessages(get()) }

        addSingletonFactory<LibraryUpdateErrorRepository> { LibraryUpdateErrorRepositoryImpl(get()) }
        addFactory { GetLibraryUpdateErrors(get()) }
        addFactory { DeleteLibraryUpdateErrors(get()) }
        addFactory { InsertLibraryUpdateErrors(get()) }

        // KMK --> AI Recommendations
        addSingletonFactory<RecommendationsRepository> { RecommendationsRepositoryImpl(get()) }
        addFactory { GetRecommendations(get()) }
        addFactory { GetBecauseYouReadRecommendations(get()) }
        addFactory { TrackReadingHistory(get()) }
        addFactory { GetReadingHistory(get()) }
        addFactory { SyncMangaTags(get()) }
        addFactory { UpdateRecommendationsCache(get()) }
        addFactory { ClearOldRecommendations(get()) }
        // KMK <--

        // KMK --> Feature repositories
        addSingletonFactory { SmartBrightnessRepository(get()) }
        addSingletonFactory { ReadingStatsRepository(get()) }
        addSingletonFactory { SearchHistoryRepository(get()) }
        addSingletonFactory { SearchSuggestionsRepository(get()) }
        addSingletonFactory { SavedSearchRepository(get()) }
        addSingletonFactory { SearchRankingRepository() }
        // KMK <--
    }
}
