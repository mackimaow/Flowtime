package contextModules

import data.UserSettings
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.launch
import react.*
import scope
import addSessionHeader
import reactUtils.ExternalResource

suspend fun UserSettings.Companion.delete() {
    val response = AppClient.http.delete(URL_PATH) {
        addSessionHeader()
    }
    AppClient.doOrRedirect(response)
}

suspend fun UserSettings.Companion.set(settings: UserSettings) {
    val response = AppClient.http.post(URL_PATH){
        addSessionHeader()
        contentType(ContentType.Application.Json)
        setBody(settings)
    }
    AppClient.doOrRedirect(response)
}

suspend fun UserSettings.Companion.get(): UserSettings {
    val response = AppClient.http.get(URL_PATH) {
        addSessionHeader()
    }
    return AppClient.getOrRedirect(response)
}

typealias SettingsState = ExternalResource<UserSettings?>

val SettingsContext = createContext<SettingsState>()

external interface SettingsProviderProps: PropsWithChildren {
    var initializedState: StateInstance<Boolean>
}

val SettingsProvider = FC<SettingsProviderProps> { props ->
    var initialized by props.initializedState
    val settingsState = ExternalResource.useState<UserSettings?>(
        null
    ) { newValue, setter ->
        scope.launch {
            UserSettings.set(newValue!!)
            setter(newValue)
        }
    }
    var settings by settingsState

    useEffect(settings) {
        if (settings == null) {
            scope.launch {
                if (!AppClient.initialized)
                    AppClient.initializeClient()
                settings = UserSettings.get()
                initialized = true
            }
        }
    }

    SettingsContext(settingsState) {
        +props.children
    }
}