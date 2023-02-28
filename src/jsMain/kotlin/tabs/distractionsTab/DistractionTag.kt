package tabs.distractionsTab

import contextModules.ThemeContext
import csstype.TextShadow
import csstype.px
import data.Distraction
import kotlinx.datetime.toJSDate
import mui.icons.material.Clear
import mui.material.*
import mui.system.responsive
import mui.system.sx
import react.*

external interface DistractionTagProps: Props {
    var distraction: Distraction
}

val DistractionTag = FC<DistractionTagProps> { props ->
    val theme by useContext(ThemeContext)
    val tagColor = props.distraction.tagColor
    val tag = props.distraction.tag
    val description = props.distraction.description
    val created = props.distraction.created.toJSDate().toLocaleString()

    Tooltip {
        title = Box.create {
            Typography {
                + description
            }
            Typography {
                sx {
                    color = theme.palette.secondary.light
                }
                + "(created: $created)"
            }
        }
        Chip {
            sx {
                backgroundColor = tagColor.asDynamic()
            }
            label = Stack.create {
                direction = responsive(StackDirection.row)
                spacing = responsive(1)
                Typography {
                    sx {
                        textShadow = TextShadow(
                            1.px,
                            1.px,
                            2.px,
                            "#1e1e1e".asDynamic()
                        )
                    }
                    +tag
                }
            }
        }
    }
}