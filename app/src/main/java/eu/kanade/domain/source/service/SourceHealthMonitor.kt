package eu.kanade.domain.source.service

import eu.kanade.tachiyomi.source.Source
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Tracks source health (success/failure rates) for monitoring.
 */
@Singleton
class SourceHealthMonitor @Inject constructor() {
    private val _healthData = MutableStateFlow(mapOf<Long, SourceHealth>())
    val healthData: Flow<Map<Long, SourceHealth>> = _healthData.asStateFlow()

    /**
     * Records a successful request to a source.
     */
    fun recordSuccess(sourceId: Long, sourceName: String) {
        _healthData.update { data ->
            val current = data[sourceId] ?: SourceHealth(sourceId, sourceName)
            data + (sourceId to current.copy(
                lastSuccess = Instant.now(),
                successCount = current.successCount + 1,
            ))
        }
    }

    /**
     * Records a failed request to a source.
     */
    fun recordFailure(sourceId: Long, sourceName: String, error: Throwable) {
        _healthData.update { data ->
            val current = data[sourceId] ?: SourceHealth(sourceId, sourceName)
            data + (sourceId to current.copy(
                lastFailure = Instant.now(),
                failureCount = current.failureCount + 1,
                lastError = error.message,
            ))
        }
    }

    /**
     * Gets health status for a source.
     */
    fun getHealth(sourceId: Long): SourceHealth? = _healthData.value[sourceId]

    /**
     * Clears old health data.
     */
    fun clearOldData(olderThanDays: Int = 7) {
        val cutoff = Instant.now().minusSeconds(olderThanDays * 24 * 60 * 60L)
        _healthData.update { data ->
            data.filterValues { health ->
                (health.lastSuccess?.isAfter(cutoff) ?: false) ||
                (health.lastFailure?.isAfter(cutoff) ?: false)
            }
        }
    }

    data class SourceHealth(
        val sourceId: Long,
        val sourceName: String,
        val lastSuccess: Instant? = null,
        val lastFailure: Instant? = null,
        val successCount: Int = 0,
        val failureCount: Int = 0,
        val lastError: String? = null,
    ) {
        val successRate: Float
            get() {
                val total = successCount + failureCount
                return if (total > 0) successCount.toFloat() / total else 1f
            }

        val status: HealthStatus
            get() = when {
                failureCount == 0 && successCount > 0 -> HealthStatus.HEALTHY
                successRate > 0.8f -> HealthStatus.DEGRADED
                successRate > 0.5f -> HealthStatus.UNSTABLE
                else -> HealthStatus.DOWN
            }

        val lastChecked: Instant?
            get() = listOfNotNull(lastSuccess, lastFailure).maxOrNull()
    }

    enum class HealthStatus {
        HEALTHY,    // Green - working well
        DEGRADED,   // Yellow - occasional failures
        UNSTABLE,   // Orange - frequent failures
        DOWN,       // Red - mostly failing
        UNKNOWN,    // Gray - no data
    }
}
