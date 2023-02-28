import kotlinx.js.jso
import mui.material.PaletteMode
import mui.material.styles.createTheme

object AppThemes {
    val Light = createTheme(
        jso {
            palette = jso { mode = PaletteMode.light }
        }
    )

    val Dark = createTheme(
        jso {
            palette = jso { mode = PaletteMode.dark }
        }
    )
}