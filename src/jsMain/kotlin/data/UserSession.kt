package data

import addSessionHeader
import io.ktor.client.request.*

suspend fun UserSession.Companion.shutdown() {
    val response = AppClient.http.post("$URL_PATH/shutdown"){
        addSessionHeader()
    }
    AppClient.doOrRedirect(response)
}