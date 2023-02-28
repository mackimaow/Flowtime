import data.UserSession
import io.ktor.http.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.browser.window
import kotlinx.coroutines.delay

object AppClient {
    val http = HttpClient {
        install(ContentNegotiation) {
            json()
        }
    }

    var initialized = false
        private set

    private var initializing = false // this is to prevent coroutines from initializing twice

    var sessionSerialized: String? = null
        private set

    var session: UserSession? = null
        private set

    private inline fun initialize(initializeBody: () -> Unit) {
        try {
            initializing = true
            initializeBody()
            initialized = true
        } finally {
            initializing = false
        }
    }

    // can be called as many times as the user wants, but will only initialize once.
    // If another coroutine is in the middle of executing this function,
    //  the current coroutine will wait until it's finished
    suspend fun initializeClient() {
        while (initializing) {
            delay(100)
        }
        if (!initialized) {
            initialize {
                val response = http.post(UserSession.URL_PATH) {
                    contentType(ContentType.Application.Json)
                    setBody(UserSession())
                }
                session = response.body()
                sessionSerialized = response.headers[UserSession.URL_PATH]
            }
        }
    }

    suspend inline fun <reified T> getOrRedirect(response: HttpResponse): T {
        if (response.status == HttpStatusCode.Forbidden) {
            window.location.reload()
            throw RuntimeException("Cannot authenticate session. Redirection is in progress.")
        }
        return response.body()
    }

    fun doOrRedirect(response: HttpResponse) {
        if (response.status == HttpStatusCode.Forbidden) {
            window.location.reload()
            throw RuntimeException("Cannot authenticate session. Redirection is in progress.")
        }
    }
}

fun HttpRequestBuilder.addSessionHeader() {
    if (!AppClient.initialized)
        window.location.reload()
    header(UserSession.URL_PATH, AppClient.sessionSerialized)
}