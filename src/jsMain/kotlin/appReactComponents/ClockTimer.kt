package appReactComponents

import csstype.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import mui.material.Paper
import mui.material.Typography
import mui.material.styles.TypographyVariant
import mui.system.sx
import react.*
import scope
import kotlin.time.DurationUnit
import kotlin.time.toDuration

enum class CountMode {
    CountUp,
    CountDown
}

external interface ClockTimerProps: Props {
    var seconds: Long
    var mode: CountMode?
    var pause: Boolean?
    var onChangeCount: ((Long, Boolean) -> Unit)?
    var counterColor: Color?
}

var ClockTimer = FC<ClockTimerProps> { props ->
    var referenceTime: Instant? by useState(null)

    useEffect(props.pause, referenceTime, props.mode, props.seconds, props.onChangeCount) {
        if (!props.pause!!) {
            if (referenceTime == null) {
                referenceTime = Clock.System.now()
            } else {
                var wasCanceled = false
                scope.launch {
                    delay(100L)
                    if (!wasCanceled) {
                        val currentTime = Clock.System.now()
                        val timeDifference = (currentTime - referenceTime!!).inWholeSeconds
                        referenceTime = referenceTime!! + timeDifference.toDuration(DurationUnit.SECONDS)
                        if (timeDifference != 0L) {
                            val newSeconds = if (props.mode!! == CountMode.CountUp)
                                props.seconds + timeDifference
                            else
                                props.seconds - timeDifference
                            props.onChangeCount!!(
                                newSeconds,
                                props.seconds != newSeconds && newSeconds == 0L
                            )
                        }
                    }
                }
                cleanup {
                    wasCanceled = true
                }
            }
        } else {
            if (referenceTime != null)
                referenceTime = null
        }
    }

    fun getHours(seconds: Long): Long {
        return seconds / (60L * 60L)
    }
    fun getMinutes(seconds: Long): Long {
        return seconds / 60L - getHours(seconds) * 60L
    }
    fun getSeconds(seconds: Long): Long {
        return seconds % 60L
    }

    fun hoursString(seconds: Long): String {
        return "${getHours(seconds)}".padStart(2, '0')
    }

    fun minutesString(seconds: Long): String {
        return "${getMinutes(seconds)}".padStart(2, '0')
    }

    fun secondsString(seconds: Long): String {
        return "${getSeconds(seconds)}".padStart(2, '0')
    }

    fun displayTimeString(seconds: Long): String {
        return if (seconds < 0L) {
            val pSeconds = -seconds
            "-${hoursString(pSeconds)}:${minutesString(pSeconds)}:${secondsString(pSeconds)}"
        } else {
            "${hoursString(props.seconds)}:${minutesString(props.seconds)}:${secondsString(props.seconds)}"
        }
    }

    Paper {
        elevation = 4
        sx {
            padding = Padding(10.px, 10.px)
            overflow = Overflow.hidden
        }
        Typography {
            variant = TypographyVariant.h4
            sx {
                textAlign = TextAlign.center
                if (props.counterColor != null)
                    color = props.counterColor
            }
            + displayTimeString(props.seconds)
        }
    }

}