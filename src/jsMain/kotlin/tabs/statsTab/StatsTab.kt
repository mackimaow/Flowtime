package tabs.statsTab

import appReactComponents.*
import csstype.*
import data.Distraction
import data.FlowTimeEventStatistics
import data.StatTimePeriodType
import data.get
import externalAdditions.mui.contentCentered
import externalAdditions.tanstackTable.buildColumns
import kotlinx.coroutines.launch
import mui.icons.material.Refresh
import mui.material.*
import mui.material.styles.TypographyVariant
import mui.system.sx
import react.FC
import react.Props
import react.create
import react.dom.html.ReactHTML.i
import react.useState
import reactUtils.ReactBox
import scope
import tabs.distractionsTab.DistractionTag
import tanstack.table.core.RowData
import kotlin.time.DurationUnit
import kotlin.time.toDuration

external interface StatsTabContentProps: Props {}


data class StatsRow(
    val statName: String,
    val timeframe: String,
    val timesOccurred: Long,
    val totalValue: Long,
    val averageValue: Long,
    val minValue: Long,
    val maxValue: Long,
    val stdDeviation: Long,
    val distraction: Distraction? = null
): RowData

val StatsTabContent = FC<StatsTabContentProps> {
    val rowData by ReactBox.useState(listOf<StatsRow>())
    var refreshing by useState(false)

    fun refreshData() {
        refreshing = true
        scope.launch {
            val stats = FlowTimeEventStatistics.get()
            val statRows = listOf (
                "Work Session Elapsed Time" to stats.workSessionElapsedTime,
                "Break Session Elapsed Time" to stats.breakSessionElapsedTime,
                "Break Session Defaulted Time" to stats.breakSessionDefaultedTime,
            ).map { (statName, timePeriodStats) ->
                listOf(
                    StatTimePeriodType.TODAY to timePeriodStats.today,
                    StatTimePeriodType.PAST_WEEK to timePeriodStats.pastWeek,
                    StatTimePeriodType.PAST_MONTH to timePeriodStats.pastMonth,
                    StatTimePeriodType.PAST_YEAR to timePeriodStats.pastYear
                ).map { (timeFrameType, stat) ->
                    StatsRow(
                        statName = statName,
                        timeframe = timeFrameType.displayText,
                        timesOccurred = stat.timesOccurred,
                        totalValue = stat.totalValue,
                        averageValue = stat.averageValue,
                        minValue = stat.minValue,
                        maxValue = stat.maxValue,
                        stdDeviation = stat.stdDeviation,
                    )
                }
            }.fold(mutableListOf<StatsRow>()) { list, statRow ->
                list.addAll(statRow)
                list
            }
            val distractionStatRows = stats.distractionStats.map { (distraction, timePeriodStats)->
                listOf(
                    StatTimePeriodType.TODAY to timePeriodStats.today,
                    StatTimePeriodType.PAST_WEEK to timePeriodStats.pastWeek,
                    StatTimePeriodType.PAST_MONTH to timePeriodStats.pastMonth,
                    StatTimePeriodType.PAST_YEAR to timePeriodStats.pastYear
                ).map { (timeFrameType, stat) ->
                    StatsRow(
                        statName = distraction.tag,
                        timeframe = timeFrameType.displayText,
                        timesOccurred = stat.timesOccurred,
                        totalValue = stat.totalValue,
                        averageValue = stat.averageValue,
                        minValue = stat.minValue,
                        maxValue = stat.maxValue,
                        stdDeviation = stat.stdDeviation,
                        distraction = distraction
                    )
                }
            }.fold(mutableListOf<StatsRow>()) { list, statRow ->
                list.addAll(statRow)
                list
            }
            statRows.addAll(distractionStatRows)
            rowData.update {
                statRows
            }
            refreshing = false
        }
    }

    AppBackdrop {
        open = refreshing
    }

    val columns = buildColumns<StatsRow> {
        accessor { it: StatsRow -> it.statName }.column {
            val propertyString = "Statistic Name"
            id = propertyString
            header {
                Typography.create {
                    + propertyString
                    SortButton {
                        column = it.column
                        disabled = false
                    }
                }
            }
            cell {
                val distraction = it.row.original.distraction
                if (distraction != null) {
                    DistractionTag.create {
                        this.distraction = distraction
                    }
                } else {
                    Typography.create {
                        + "${it.getValue()}"
                    }
                }
            }
        }
        listOf (
            "Timeframe" to { it: StatsRow -> it.timeframe },
            "Occurrences" to { it: StatsRow -> it.timesOccurred },
        ).forEach { (propertyString, property) ->
            accessor(property).column {
                id = propertyString
                header {
                    Typography.create {
                        + propertyString
                        SortButton {
                            column = it.column
                            disabled = false
                        }
                    }
                }
                cell {
                    Typography.create {
                        + "${it.getValue()}"
                    }
                }
            }
        }
        listOf(
            "Cumulative Sum" to { it: StatsRow -> it.totalValue },
            "Average" to { it: StatsRow -> it.averageValue },
            "Minimum" to { it: StatsRow -> it.minValue },
            "Maximum" to { it: StatsRow -> it.maxValue },
            "Standard Deviation" to { it: StatsRow -> it.stdDeviation },
        ).forEach { (propertyString, property) ->
            accessor(property).column {
                id = propertyString
                header {
                    Typography.create {
                        + propertyString
                        SortButton {
                            column = it.column
                            disabled = false
                        }
                    }
                }
                cell {
                    val durationLong = it.getValue()
                    val duration = durationLong.toDuration(DurationUnit.SECONDS)
                    Typography.create {
                        + "$duration"
                    }
                }
            }
        }
    }

    Box {
        contentCentered = true
        sx {
            width = 100.pct
            padding = 10.px
        }
        Paper {
            sx {
                padding = 20.px
                minWidth = 0.px
            }
            Typography {
                sx {
                    padding = 20.px
                }
                variant = TypographyVariant.h3
                + "Statistics"
                IconButton {
                    Refresh {}
                    onClick = {
                        refreshData()
                    }
                }
            }
            Divider {
                sx {
                    marginBottom = 20.px
                }
            }
            if (rowData.value.isEmpty()) {
                Typography {
                    i {
                        + "No data to show. Please click the refresh button to (re)load Statistics."
                    }
                }
            } else {
                DataGrid {
                    this.columns = columns
                    data = rowData.asDynamic()
                    hasHeader = true
                    hasBody = true
                    hasFooter = false
                    tableContainerSx {
                        margin = 5.px
                        padding = 10.px
                        minWidth = 0.px
                    }
                    tableSx {
                        overflowX = Auto.auto
                    }
                }
            }
        }
    }
}