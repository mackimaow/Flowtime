@file:JsModule("@mui/material/Unstable_Grid2")
@file:JsNonModule
package externalAdditions.mui

import mui.material.GridProps
import mui.system.ResponsiveStyleValue
import react.FC


external interface Grid2Props: GridProps {
    var xs: Int?
    var sm: Int?
    var md: Int?
    var lg: Int?
    var xl: Int?
}


@JsName("default")
external var Grid2: FC<Grid2Props>