package tabs.settingsTab

import csstype.px
import mui.material.Box
import mui.material.Slider
import mui.material.Typography
import mui.system.sx
import react.FC
import react.Props

external interface SetWorkBreakRatioProps: Props {
    var newRatio: Int
    var actualRatio: Int
    var setRatio: (Int) -> Unit
}

var SetWorkBreakRatio = FC<SetWorkBreakRatioProps> {
    Box {
        sx {
            padding = 10.px
        }
        Typography {
            + "Work to break ratio (Current: ${it.actualRatio})"
        }
        Slider {
            value = it.newRatio
            valueLabelDisplay = "auto"
            step = 1
            marks = true
            min = 1
            max = 10
            onChange = { _, v, _ ->
                it.setRatio(v)
            }
        }
    }
}