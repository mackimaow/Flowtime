package tabs.timerTab

import contextModules.ThemeContext
import csstype.Display
import csstype.FlexWrap
import csstype.px
import data.FlowSession
import externalAdditions.mui.contentCentered
import kotlinx.datetime.Clock
import kotlinx.datetime.toJSDate
import mui.icons.material.*
import mui.material.*
import mui.material.styles.TypographyVariant
import mui.system.responsive
import mui.system.sx
import react.*
import react.dom.html.ReactHTML.i
import react.dom.html.ReactHTML.span
import tabs.distractionsTab.DistractionTag
import kotlin.time.DurationUnit
import kotlin.time.toDuration

enum class ChangeOption {
    DELETE,
    EDIT;
}

external interface FlowSessionPaneProps: Props {
    var flowSession: FlowSession
    var onChange: (FlowSession, ChangeOption) -> Unit
}

val FlowSessionPane = FC<FlowSessionPaneProps> { props ->
    val theme by useContext(ThemeContext)
    var editDialogOpen by useState(false)
    var deleteDialogOpen by useState(false)
    var titleHovered by useState(false)

    RemoveFlowSessionDialog {
        flowSession = props.flowSession
        open = deleteDialogOpen
        onClose = { flowSession ->
            if (flowSession != null)
                props.onChange(flowSession, ChangeOption.DELETE)
            deleteDialogOpen = false
        }
    }

    Paper {
        sx {
            padding = 30.px
            margin = 10.px
        }
        elevation = 2
        if (props.flowSession.isBreak) {
            Stack {
                direction = responsive(StackDirection.row)
                spacing = responsive(2)
                onMouseOver = {
                    titleHovered = true
                }
                onMouseOut = {
                    titleHovered = false
                }
                Box {
                    contentCentered = true
                    if (titleHovered) {
                        IconButton {
                            Delete {}
                            color = IconButtonColor.error
                            onClick = {
                                deleteDialogOpen = true
                            }
                        }
                    } else {
                        Bedtime {
                            color = SvgIconColor.secondary
                        }
                    }
                }
                Typography {
                    variant = TypographyVariant.h5
                    + "Break Time"
                }
                Tooltip {
                    title = Typography.create {
                        + "Total Duration"
                    }
                    Typography {
                        sx {
                            color = theme.palette.text.secondary
                        }
                        variant = TypographyVariant.h6
                        val totalTime = props.flowSession.end - props.flowSession.start
                        + "($totalTime)"
                    }
                }
                val defaultedTime = props.flowSession.breakTimeEndAmount
                if (defaultedTime < 0) {
                    Tooltip {
                        title = Typography.create {
                            + "Defaulted Break Time"
                        }
                        Typography {
                            sx {
                                color = theme.palette.error.light
                            }
                            variant = TypographyVariant.h6

                            val rewardedTimeDuration = defaultedTime.toDuration(DurationUnit.SECONDS)
                            + "-${-rewardedTimeDuration}"
                        }
                    }
                }
            }
        } else {
            Stack {
                direction = responsive(StackDirection.row)
                spacing = responsive(2)
                onMouseOver = {
                    titleHovered = true
                }
                onMouseOut = {
                    titleHovered = false
                }
                Box {
                    contentCentered = true
                    if (titleHovered) {
                        IconButton {
                            Delete {}
                            color = IconButtonColor.error
                            onClick = {
                                deleteDialogOpen = true
                            }
                        }
                    } else {
                        Work {
                            color = SvgIconColor.success
                        }
                    }
                }
                Typography {
                    variant = TypographyVariant.h5
                    + "Work Time"
                }

                Tooltip {
                    title = Typography.create {
                        + "Total Duration"
                    }
                    Typography {
                        sx {
                            color = theme.palette.text.secondary
                        }
                        variant = TypographyVariant.h6
                        val totalTime = props.flowSession.end - props.flowSession.start
                        +"($totalTime)"
                    }
                }
                Tooltip {
                    title = Typography.create {
                        + "Rewarded Break Time"
                    }
                    Typography {
                        sx {
                            color = theme.palette.success.light
                        }
                        variant = TypographyVariant.h6

                        val rewardedTime = (
                            props.flowSession.breakTimeEndAmount
                            - props.flowSession.breakTimeStartAmount
                        ).toDuration(DurationUnit.SECONDS)
                        + "+$rewardedTime"
                    }
                }
            }
        }

        Divider {
            sx {
                marginTop = 15.px
                marginBottom = 15.px
            }
        }

        val start = props.flowSession.start.toJSDate()
        val end = props.flowSession.end.toJSDate()
        val now = Clock.System.now().toJSDate()
        val isStartToday = start.getDate() == now.getDate()
        val isEndToday = end.getDate() == now.getDate()
        Box {
            sx {
                marginLeft = 15.px
            }
            if (start.toDateString() == end.toDateString()) {
                Typography {
                    variant = TypographyVariant.subtitle2
                    sx {
                        color = theme.palette.text.secondary
                    }
                    if (isStartToday) {
                        + "Occurred Today (${start.toDateString()})"
                    } else {
                        + "Occurred on ${start.toDateString()}"
                    }
                }
                Typography {
                    variant = TypographyVariant.subtitle2
                    sx {
                        color = theme.palette.text.secondary
                    }
                    + "From: ${start.toTimeString()}"
                }
                Typography {
                    variant = TypographyVariant.subtitle2
                    sx {
                        color = theme.palette.text.secondary
                    }
                    + "To: ${end.toTimeString()}"
                }

            } else {
                Typography {
                    variant = TypographyVariant.subtitle2
                    sx {
                        color = theme.palette.text.secondary
                    }
                    + "Occurred"
                }
                if (isEndToday) {
                    Typography {
                        variant = TypographyVariant.subtitle2
                        sx {
                            color = theme.palette.text.secondary
                        }
                        + "From: ${start.toDateString()} (${start.toTimeString()})"
                    }
                    Typography {
                        variant = TypographyVariant.subtitle2
                        sx {
                            color = theme.palette.text.secondary
                        }
                        + "To: Today ${end.toDateString()} (${end.toTimeString()})"
                    }
                } else {
                    Typography {
                        variant = TypographyVariant.subtitle2
                        sx {
                            color = theme.palette.text.secondary
                        }
                        + "From: ${start.toDateString()} (${start.toTimeString()})"
                    }
                    Typography {
                        variant = TypographyVariant.subtitle2
                        sx {
                            color = theme.palette.text.secondary
                        }
                        + "To: ${end.toDateString()} (${end.toTimeString()})"
                    }
                }
            }
        }
        Stack {
            sx {
                marginTop = 20.px
            }
            direction = responsive(StackDirection.row)
            spacing = responsive(2)
            Typography {
                sx {
                    marginTop = 5.px
                }
                variant = TypographyVariant.h6
                + "Distractions"
            }
            IconButton {
                Edit {}
                onClick = {
                    editDialogOpen = true
                }
            }
        }

        if (props.flowSession.distractions.isEmpty()) {
            Typography {
                sx {
                    color = theme.palette.text.secondary
                    marginTop = 10.px
                    marginLeft = 10.px
                }
                i {
                    + "No distractions are present here"
                }
            }
        } else {
            Paper {
                sx {
                    padding = 20.px
                    margin = 10.px
                }
                Box {
                    sx {
                        display = Display.flex
                        flexWrap = FlexWrap.wrap
                        gap = 0.5.px
                    }
                    for (distraction in props.flowSession.distractions) {
                        Box {
                            component = span
                            sx {
                                marginTop = 5.px
                                marginRight = 5.px
                            }
                            key = "${distraction.id}"
                            DistractionTag {
                                this.distraction = distraction
                            }
                        }
                    }
                }
            }
        }
        AddRemoveDistractionDialog {
            open = editDialogOpen
            this.flowSession = props.flowSession
            onClose = { updatedFlowSession ->
                if(updatedFlowSession != null)
                    props.onChange(updatedFlowSession, ChangeOption.EDIT)
                editDialogOpen = false
            }
        }
    }
}