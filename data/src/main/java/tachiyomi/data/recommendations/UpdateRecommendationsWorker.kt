package tachiyomi.data.recommendations

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mihon.domain.recommendation.repository.RecommendationsRepository
import tachiyomi.core.common.util.system.logcat
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get
import java.util.Date
import java.util.concurrent.TimeUnit

/**
 * Worker that periodically updates recommendations cache.
 * Runs weekly to refresh personalized recommendations.
 */
class UpdateRecommendationsWorker(
    context: Context,
    params: WorkerParameters,
    private val repository: RecommendationsRepository = Injekt.get(),
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            logcat { "Starting recommendations update..." }
            
            withContext(Dispatchers.IO) {
                // Clear old recommendations
                val oneWeekAgo = Date(System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000)
                repository.deleteOldRecommendations(oneWeekAgo)
                
                // Generate new personalized recommendations
                val recommendations = repository.getPersonalizedRecommendations(limit = 20)
                
                // Cache the recommendations
                recommendations.forEach { rec ->
                    // Store in cache with a generic source manga ID (0 for general recommendations)
                    repository.insertRecommendationCache(
                        mangaId = 0,
                        recommendedMangaId = rec.mangaId,
                        score = rec.score,
                        reason = rec.reason,
                    )
                }
                
                // Generate "Because you read" recommendations
                val becauseYouRead = repository.getBecauseYouReadRecommendations(limitPerSource = 5)
                becauseYouRead.forEach { group ->
                    group.recommendations.forEach { rec ->
                        repository.insertRecommendationCache(
                            mangaId = group.sourceMangaId,
                            recommendedMangaId = rec.mangaId,
                            score = rec.score,
                            reason = rec.reason,
                        )
                    }
                }
            }
            
            logcat { "Recommendations update completed successfully" }
            Result.success()
        } catch (e: Exception) {
            logcat { "Recommendations update failed: ${e.message}" }
            Result.retry()
        }
    }

    companion object {
        private const val WORK_NAME = "update_recommendations"
        
        /**
         * Schedule the periodic recommendations update job.
         * Runs once per week when the device has network connectivity.
         */
        fun schedule(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true)
                .build()
            
            val request = PeriodicWorkRequestBuilder<UpdateRecommendationsWorker>(
                7, TimeUnit.DAYS, // Repeat every 7 days
                6, TimeUnit.HOURS, // Flex interval
            ).setConstraints(constraints)
                .addTag(WORK_NAME)
                .build()
            
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request,
            )
            
            logcat { "Scheduled recommendations update job" }
        }
        
        /**
         * Cancel the scheduled recommendations update job.
         */
        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        }
        
        /**
         * Run the update immediately (one-time execution).
         */
        fun runNow(context: Context) {
            val request = androidx.work.OneTimeWorkRequestBuilder<UpdateRecommendationsWorker>()
                .addTag("$WORK_NAME-once")
                .build()
            
            WorkManager.getInstance(context).enqueue(request)
        }
    }
}
