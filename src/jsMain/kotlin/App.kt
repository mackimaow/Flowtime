import contextModules.*
import kotlinx.coroutines.MainScope
import react.*


val scope = MainScope()


val App = FC<Props> {
    val dataInitializedState = useState(false)
    val dataInitialized by dataInitializedState

    val settingsInitializedState = useState(false)
    val settingsInitialized by settingsInitializedState

    AppCookieProvider {
        AppThemeProvider {
            DataProvider {
                initializedState = dataInitializedState
                SettingsProvider {
                    initializedState = settingsInitializedState
                    InitializedModule {
                        initialized = dataInitialized && settingsInitialized
                        DistractionsProvider {
                            AppContent {}
                        }
                    }
                }
            }
        }
    }
}