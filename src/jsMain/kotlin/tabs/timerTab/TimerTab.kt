package tabs.timerTab

import AcknowledgeComplete
import PromptContinueDialog
import contextModules.BufferedContext
import contextModules.DataContext
import csstype.*
import kotlinx.datetime.Clock
import mui.material.*
import mui.material.styles.TypographyVariant
import mui.system.responsive
import mui.system.sx
import externalAdditions.mui.Grid2
import react.*
import reactUtils.ReactBox
import kotlin.time.DurationUnit
import kotlin.time.toDuration

var TimerTab = FC<Props> {
    // this buffer is used to prevent bugs on onBreakEnd -> onWorkStart and
    //  and onWorkEnd -> onBreakStart transitions,
    //  since userData doesn't update until coroutine finishes and
    //  transitions callbacks pairs --(onBreakEnd, onWorkStart) and (onWorkEnd, onBreakStart)--
    //  are called in the same coroutine.
    //  Buffered context keeps a local copy of userData to be used.
    var userData by BufferedContext.use(DataContext)

    Box {
        sx {
            padding = 10.px
        }
        Grid2 {
            container = true
            spacing = responsive(2)
            Grid2 {
                xs = 12
                md = 6
                xl = 3
                Paper {
                    sx {
                        padding = Padding(20.px, 20.px)
                    }
                    Typography {
                        sx {
                            padding = 20.px
                        }
                        variant = TypographyVariant.h3
                        + "Timer"
                    }
                    Divider {}
                    Timers {
                        onWorkStart = { breakSecondsLeft ->
                            userData?.copy(
                                workTimerEnabled = true,
                                timersOn = true,
                                timerStart = Clock.System.now(),
                                startBreakSeconds = breakSecondsLeft.toInt(),
                                endBreakSeconds = 0
                            )?.also {
                                userData = it
                            }
                        }
                        onWorkEnd = { breakSecondsLeft ->
                            userData?.copy(
                                endBreakSeconds = breakSecondsLeft.toInt()
                            )?.also {
                                userData = it
                            }
                        }
                        onBreakStart = { secondsStarted ->
                            userData?.copy(
                                workTimerEnabled = false,
                                timersOn = true,
                                timerStart = Clock.System.now(),
                                startBreakSeconds = secondsStarted.toInt(),
                                endBreakSeconds = 0
                            )?.also {
                                userData = it
                            }
                        }
                        onBreakEnd = { secondsLeft ->
                            userData?.copy(
                                endBreakSeconds = secondsLeft.toInt()
                            )?.also {
                                userData = it
                            }
                        }
                        onBreakReset = {
                            userData?.copy(
                                startBreakSeconds = 0
                            )?.also {
                                userData = it
                            }
                        }
                    }
                }
            }
            Grid2 {
                xs = 12
                md = 6
                xl = 3
                Paper {
                    sx {
                        padding = Padding(20.px, 20.px)
                    }
                    Typography {
                        sx {
                            padding = 20.px
                        }
                        variant = TypographyVariant.h3
                        + "Flow Sessions"
                    }
                    Divider {
                        sx {
                            marginBottom = 20.px
                        }
                    }
                    PreviousFlowSessionPane {}
                }
            }
        }
    }
}