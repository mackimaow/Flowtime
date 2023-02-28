package data

import addSessionHeader
import io.ktor.client.request.*

suspend fun FlowTimeEventStatistics.Companion.get(): FlowTimeEventStatistics {
    val response = AppClient.http.get(URL_PATH) {
        addSessionHeader()
    }
    return AppClient.getOrRedirect(response)
}