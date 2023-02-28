package data

import addSessionHeader
import io.ktor.client.request.*
import io.ktor.http.*


suspend fun FlowSession.Companion.getAll(): List<FlowSession> {
    val response = AppClient.http.get(URL_PATH) {
        addSessionHeader()
    }
    return AppClient.getOrRedirect(response)
}

suspend fun FlowSession.Companion.getLatest(amount: Int): List<FlowSession> {
    val response = AppClient.http.get("$URL_PATH/latest/$amount") {
        addSessionHeader()
    }
    return AppClient.getOrRedirect(response)
}

suspend fun FlowSession.Companion.set(flowSession: FlowSession) {
    val response = AppClient.http.post(URL_PATH){
        addSessionHeader()
        contentType(ContentType.Application.Json)
        setBody(flowSession)
    }
    AppClient.doOrRedirect(response)
}

suspend fun FlowSession.Companion.delete(flowSession: FlowSession) {
    val response = AppClient.http.delete("$URL_PATH/${flowSession.id}"){
        addSessionHeader()
    }
    AppClient.doOrRedirect(response)
}