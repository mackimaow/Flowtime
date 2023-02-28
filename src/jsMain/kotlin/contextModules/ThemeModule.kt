package contextModules

import AppThemes
import mui.material.CssBaseline
import mui.material.styles.Theme
import mui.material.styles.ThemeProvider
import react.*

typealias ThemeState = StateInstance<Theme>

val ThemeContext = createContext<ThemeState>()


val AppThemeProvider = FC<PropsWithChildren> { props ->
    val state = useState(AppThemes.Dark)
    val (theme) = state

    // exposes theme context to our app components
    ThemeContext(state) {

        // exposes theme context to mui app components
        ThemeProvider {
            this.theme = theme

            CssBaseline()
            +props.children
        }
    }
}