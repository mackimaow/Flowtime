package data

import database.AppDatabase
import database.tables.Distraction
import database.tables.FlowSessionTable
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.not
import org.jetbrains.exposed.sql.select
import withSession
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.temporal.TemporalAdjusters
import java.util.Collections.max
import java.util.Collections.min
import kotlin.math.abs
import kotlin.math.sqrt

private val TODAY_TEMPORAL_ADUJUSTER = null



private data class DistractionTally(
    var timesUsed: Long = 0,
    val durations: MutableList<Long> = mutableListOf()
)

private fun toEventStats(sessionTimesList: List<Long>, count: Long? = null): EventStatistics {
    val size = sessionTimesList.size
    val sum = sessionTimesList.sum()
    val minimum = if (size != 0) min(sessionTimesList) else 0
    val maximum = if (size != 0) max(sessionTimesList) else 0
    val average = if (size != 0) sum / size else 0
    val variance = if (size != 0)
        sessionTimesList.fold(0L) { s, v ->
            s + (v-average) * (v-average)
        } / size
    else 0
    val std = sqrt(variance.toDouble()).toLong()
    return EventStatistics (
        timesOccurred = count ?: size.toLong(),
        totalValue = sum,
        averageValue = average,
        minValue = minimum,
        maxValue = maximum,
        stdDeviation = std
    )
}

private suspend fun getEventStats(): FlowTimeEventStatistics {
    return AppDatabase.transaction {
        val rightNowDate = LocalDate.now()
        val distractionsToTally = Distraction.all().associate { distraction ->
            Distraction.morph(distraction) to StatTimePeriodType.values().associateWith {
                DistractionTally()
            }
        }
        val eventStats = listOf(
            StatTimePeriodType.TODAY to TODAY_TEMPORAL_ADUJUSTER, // TODAY
            StatTimePeriodType.PAST_WEEK to TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY), // Past Week
            StatTimePeriodType.PAST_MONTH to TemporalAdjusters.firstDayOfMonth(), // Past Month
            StatTimePeriodType.PAST_YEAR to TemporalAdjusters.firstDayOfYear(), // Past Year
        ).map { (timeFrameEnum, timeFrame) ->
            val lowerBoundLocalDate = if (timeFrame == TODAY_TEMPORAL_ADUJUSTER) {
                rightNowDate
            } else {
                rightNowDate.with(timeFrame)
            }
            val lowerBound = lowerBoundLocalDate.atTime(0, 0)
                .atZone(ZoneId.systemDefault())
                .withZoneSameInstant(ZoneOffset.UTC)
                .toLocalDateTime()

            val eventStats = listOf(
                true to (FlowSessionTable.isBreak),
                false to not (FlowSessionTable.isBreak)
            ).map { (isBreak, typeCondition) ->

                val (sessionTimes, defaultedTimes) = FlowSessionTable.select {
                    (FlowSessionTable.start greaterEq lowerBound) and typeCondition
                }.map { row ->
                    val sessionEntry = database.tables.FlowSession.wrapRow(row)
                    val session = database.tables.FlowSession.morph(sessionEntry)
                    val durationSeconds = (session.end - session.start).inWholeSeconds
                    if (isBreak) {
                        session.distractions.forEach {
                            distractionsToTally[it]
                                ?.get(timeFrameEnum)?.also { distractionTally ->
                                    distractionTally.durations.add(durationSeconds)
                                    distractionTally.timesUsed += 1
                                }
                        }
                    } else {
                        session.distractions.forEach {
                            distractionsToTally[it]
                                ?.get(timeFrameEnum)
                                ?.also { distractionTally ->
                                    distractionTally.timesUsed += 1
                                }
                        }
                    }
                    listOf(
                        durationSeconds,
                        session.breakTimeEndAmount.toLong()
                    ).zipWithNext()[0]
                }.unzip()
                if (isBreak) {
                    listOf(
                        sessionTimes,
                        defaultedTimes.filter { it < 0 }.map { abs(it) }
                    )
                } else {
                    listOf(
                        sessionTimes
                    )
                }.map { sessionTimesList ->
                    toEventStats(sessionTimesList)
                }
            }
            Triple(
                eventStats[1][0],
                eventStats[0][0],
                eventStats[0][1],
            )
        }.fold(listOf(
            mutableListOf<EventStatistics>(),
            mutableListOf<EventStatistics>(),
            mutableListOf<EventStatistics>()
        )) { s, (s1, s2, s3) ->
            s[0].add(s1)
            s[1].add(s2)
            s[2].add(s3)
            s
        }.map { eventStats ->
            EventStatisticTimePeriod(
                eventStats[0],
                eventStats[1],
                eventStats[2],
                eventStats[3],
            )
        }
        val distractionStats = distractionsToTally.map { (distraction, timePeriodTally) ->
            val timePeriodStats = timePeriodTally.map { (timePeriod, tallies) ->
                timePeriod to toEventStats(tallies.durations, tallies.timesUsed)
            }.toMap()
            distraction to EventStatisticTimePeriod(
                today = timePeriodStats[StatTimePeriodType.TODAY]!!,
                pastWeek = timePeriodStats[StatTimePeriodType.PAST_WEEK]!!,
                pastMonth = timePeriodStats[StatTimePeriodType.PAST_MONTH]!!,
                pastYear = timePeriodStats[StatTimePeriodType.PAST_YEAR]!!
            )
        }.toMap()
        FlowTimeEventStatistics(
            eventStats[0],
            eventStats[1],
            eventStats[2],
            distractionStats
        )
    }
}

val FlowTimeEventStatistics.Companion.route: Route.() -> Unit
    get() = {
        route(URL_PATH) {
            get {
                withSession {
                    call.respond(getEventStats())
                }
            }
        }
    }