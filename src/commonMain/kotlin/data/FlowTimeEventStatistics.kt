package data

import kotlinx.serialization.Serializable
import kotlin.time.Duration

enum class StatTimePeriodType(val displayText: String) {
    TODAY("Today"),
    PAST_WEEK("Past Week"),
    PAST_MONTH("Past Month"),
    PAST_YEAR("Past Year")
}

@Serializable
data class EventStatistics (
    val timesOccurred: Long,
    val totalValue: Long,
    val averageValue: Long,
    val minValue: Long,
    val maxValue: Long,
    val stdDeviation: Long
)

@Serializable
data class EventStatisticTimePeriod (
    val today: EventStatistics,
    val pastWeek: EventStatistics,
    val pastMonth: EventStatistics,
    val pastYear: EventStatistics
)

@Serializable
data class FlowTimeEventStatistics (
    val workSessionElapsedTime: EventStatisticTimePeriod,
    val breakSessionElapsedTime: EventStatisticTimePeriod,
    val breakSessionDefaultedTime: EventStatisticTimePeriod,
    val distractionStats: Map<Distraction, EventStatisticTimePeriod>,
) {
    companion object {
        const val URL_PATH = "flow-time-statistics"
    }
}