package contextModules

import AppClient
import data.UserData
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.launch
import react.*
import scope
import addSessionHeader
import kotlinx.coroutines.channels.Channel
import reactUtils.ExternalResource

suspend fun UserData.Companion.set(userData: UserData) {
    val response = AppClient.http.post(URL_PATH){
        addSessionHeader()
        contentType(ContentType.Application.Json)
        setBody(userData)
    }
    AppClient.doOrRedirect(response)
}

suspend fun UserData.Companion.get(): UserData {
    val response = AppClient.http.get(URL_PATH) {
        addSessionHeader()
    }
    return AppClient.getOrRedirect(response)
}

typealias DataState = ExternalResource<UserData?>

val DataContext = createContext<DataState>()

external interface DataProviderProps: PropsWithChildren {
    var initializedState: StateInstance<Boolean>
}

internal val dataChannel = Channel<Pair<UserData?, (UserData?) -> Unit>>(
    capacity = Channel.UNLIMITED
)

// non-blocking
fun UserData.Companion.resolveDataChangesLoop() {
    scope.launch {
        for ((newValue, setter) in dataChannel) {
            UserData.set(newValue!!)
            setter(newValue)
        }
    }
}

val DataProvider = FC<DataProviderProps> { props ->
    var initialized by props.initializedState
    val dataState = ExternalResource.useState<UserData?>(
        null
    ) { newValue, setter ->
        dataChannel.trySend(
            Pair(newValue, setter)
        )
    }
    var data by dataState

    useEffect(data) {
        if (data == null) {
            scope.launch {
                if (!AppClient.initialized)
                    AppClient.initializeClient()
                data = UserData.get()
                initialized = true
            }
        }
    }

    DataContext(dataState) {
        + props.children
    }
}