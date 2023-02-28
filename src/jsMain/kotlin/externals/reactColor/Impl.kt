package externals.reactColor

import kotlinx.js.jso

external interface ColorOptions {
    val hex: String
    val rgb: RgbColor
    val hsl: HslColor
}

sealed external interface BaseColor

sealed external interface RgbColor: BaseColor {
    var r: Int
    var g: Int
    var b: Int
    var a: Int
}

sealed external interface HslColor: BaseColor {
    var h: Int
    var s: Int
    var l: Int
    var a: Int
}

object Color {
    inline fun rgb(rgbColor: RgbColor.() -> Unit): BaseColor {
        return jso(rgbColor)
    }
    inline fun hsl(hslColor: HslColor.() -> Unit): BaseColor {
        return jso(hslColor)
    }
    inline fun hex(hexColor: () -> String): BaseColor {
        return hexColor().unsafeCast<BaseColor>()
    }
}