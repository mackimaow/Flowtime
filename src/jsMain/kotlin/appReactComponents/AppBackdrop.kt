package appReactComponents

import contextModules.ThemeContext
import csstype.Color
import mui.material.Backdrop
import mui.material.CircularProgress
import mui.material.CircularProgressColor
import mui.system.sx
import react.FC
import react.Props
import react.useContext

external interface AppBackDropProps: Props {
    var open: Boolean
}

var AppBackdrop = FC<AppBackDropProps> { props ->
    val theme by useContext(ThemeContext)
    Backdrop {
        sx {
            color = Color("#fff")
            zIndex = (theme.zIndex.drawer.toInt() + 1).asDynamic()
        }
        open = props.open
        CircularProgress {
            color = CircularProgressColor.inherit
        }
    }
}