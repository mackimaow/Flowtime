package tabs.timerTab

import PromptContinueDialog
import appReactComponents.ClockTimer
import appReactComponents.CountMode
import contextModules.DataContext
import contextModules.SettingsContext
import contextModules.ThemeContext
import csstype.TextAlign
import csstype.px
import data.UserSettings
import kotlinx.datetime.Clock
import mui.material.*
import mui.material.styles.TypographyVariant
import mui.system.responsive
import mui.system.sx
import externalAdditions.mui.Grid2
import react.*
import kotlin.time.DurationUnit
import kotlin.time.toDuration

external interface TimersProps: Props {
    var onWorkStart: ((Long) -> Unit)?
    var onWorkEnd: ((Long) -> Unit)?
    var onBreakStart: ((Long) -> Unit)?
    var onBreakEnd: ((Long) -> Unit)?
    var onBreakReset: (() -> Unit)?
}

var Timers = FC<TimersProps> { props ->
    var initialized by useState(false)
    var workTimerPaused by useState(true)
    var workTimerSeconds by useState(0L)
    var breakTimerPaused by useState(true)
    var breakTimerSeconds by useState(0L)
    var baseBreakTimeSeconds by useState( 0L)
    val theme by useContext(ThemeContext)
    val userData by useContext(DataContext)
    val userSettings by useContext(SettingsContext)
    var resetBaseSecondsPromptOpen by useState(false)

    useEffect(userData) {
        if (!initialized && userData != null) {
            val timersOn = userData!!.timersOn
            val workTimerEnabled = userData!!.workTimerEnabled
            if (timersOn) {
                val secondsElapsed = userData?.timerStart?.let {
                    (Clock.System.now() - it).inWholeSeconds
                } ?: 0L
                val baseBreakSeconds = userData?.startBreakSeconds?.toLong() ?: 0L
                if (workTimerEnabled) {
                    val workBreakRatio = (userSettings?.workToBreakRatio ?: UserSettings.DEFAULT_RATIO)
                    workTimerPaused = false
                    workTimerSeconds = secondsElapsed
                    breakTimerPaused = true
                    breakTimerSeconds = baseBreakSeconds + (secondsElapsed / workBreakRatio)
                    baseBreakTimeSeconds = baseBreakSeconds
                } else {
                    workTimerPaused = true
                    workTimerSeconds = 0L
                    breakTimerPaused = false
                    breakTimerSeconds = baseBreakSeconds - secondsElapsed
                    baseBreakTimeSeconds = baseBreakSeconds
                }
            }
            initialized = true
        }
    }


    PromptContinueDialog {
        open = resetBaseSecondsPromptOpen
        title = "Reset Break Base Seconds"
        val baseDuration = baseBreakTimeSeconds.toDuration(DurationUnit.SECONDS)
        contentMessage = "Are you sure you want to reset base break of $baseDuration?"
        onClose = {
            resetBaseSecondsPromptOpen = false
        }
        onContinue = { acknowledgeDone ->
            props.onBreakReset?.also {
                it()
                baseBreakTimeSeconds = 0
                if(workTimerPaused) {
                    breakTimerSeconds -= baseBreakTimeSeconds
                }
                acknowledgeDone()
            }
        }
    }

    Grid2 {
        container = true
        spacing = responsive(2)
        sx {
            marginTop = 10.px
        }
        Grid2 {
            xs = 12
            md = 6
            Stack {
                Typography {
                    variant = TypographyVariant.h6
                    + "Work Time"
                    sx {
                        marginBottom = 10.px
                        textAlign = TextAlign.center
                    }
                }
                ClockTimer {
                    seconds = workTimerSeconds
                    mode = CountMode.CountUp
                    pause = workTimerPaused
                    counterColor = if (workTimerPaused) {
                        theme.palette.text.disabled
                    } else {
                        null // take default
                    }
                    onChangeCount = { newSeconds: Long, _ ->
                        workTimerSeconds = newSeconds
                        breakTimerSeconds = (
                            baseBreakTimeSeconds
                            + newSeconds
                            / (userSettings?.workToBreakRatio ?: UserSettings.DEFAULT_RATIO)
                        )
                    }
                }
                Button {
                    sx {
                        marginTop = 5.px
                    }
                    onClick = {
                        if (workTimerPaused) { // if break timer was active
                            if (breakTimerSeconds < 0) {
                                if (!breakTimerPaused) // if break timer was previously activated
                                    props.onBreakEnd!!(breakTimerSeconds)

                                breakTimerSeconds = 0
                                baseBreakTimeSeconds = 0

                                props.onWorkStart!!(0)
                            } else {
                                baseBreakTimeSeconds = breakTimerSeconds
                                if (!breakTimerPaused) // if break timer was previously activated
                                    props.onBreakEnd!!(breakTimerSeconds)
                                props.onWorkStart!!(breakTimerSeconds)
                            }

                            workTimerSeconds = 0
                            workTimerPaused = false
                            breakTimerPaused = true
                        } else { // if work timer was active
                            if (!workTimerPaused)
                                props.onWorkEnd!!(breakTimerSeconds)
                            if (breakTimerPaused)
                                props.onBreakStart!!(breakTimerSeconds)

                            workTimerPaused = true
                            breakTimerPaused = false
                            baseBreakTimeSeconds = breakTimerSeconds
                        }
                    }
                    if (workTimerPaused) {
                        variant =  ButtonVariant.contained
                        color = ButtonColor.success
                        Typography {
                            +"Start"
                        }
                    } else {
                        variant =  ButtonVariant.outlined
                        color = ButtonColor.error
                        Typography {
                            +"Stop"
                        }
                    }
                }
            }
        }
        Grid2 {
            xs = 12
            md = 6
            Stack {
                Typography {
                    variant = TypographyVariant.h6
                    + "Accumulated Break Time"
                    sx {
                        marginBottom = 10.px
                        textAlign = TextAlign.center
                    }
                }

                ClockTimer {
                    seconds = breakTimerSeconds
                    mode = CountMode.CountDown
                    pause = breakTimerPaused
                    counterColor = if (breakTimerPaused) {
                        theme.palette.text.disabled
                    } else {
                        if (breakTimerSeconds < 0) {
                            theme.palette.error.light
                        } else {
                            null
                        }
                    }
                    onChangeCount = { newSeconds: Long, _ ->
                        breakTimerSeconds = newSeconds
                    }
                }
                val startBreakSeconds = userData?.startBreakSeconds
                if (startBreakSeconds != 0 && startBreakSeconds != null) {

                    Tooltip {
                        title = Typography.create {
                            val breakSeconds = startBreakSeconds.toDuration(
                                DurationUnit.SECONDS
                            )
                            + "Base Seconds: $breakSeconds"
                        }
                        Button {
                            sx {
                                marginTop = 5.px
                            }
                            variant = ButtonVariant.contained
                            color = ButtonColor.error
                            onClick = {
                                resetBaseSecondsPromptOpen = true
                            }
                            + "Reset Base Seconds"
                        }
                    }
                }
            }
        }
    }
}