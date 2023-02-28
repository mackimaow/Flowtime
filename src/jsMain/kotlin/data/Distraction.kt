package data

import addSessionHeader
import io.ktor.client.request.*
import io.ktor.http.*

suspend fun Distraction.Companion.getAll(): List<Distraction> {
    val response = AppClient.http.get(URL_PATH) {
        addSessionHeader()
    }
    return AppClient.getOrRedirect(response)
}

suspend fun Distraction.Companion.set(distraction: Distraction): Distraction {
    val response = AppClient.http.post(URL_PATH){
        addSessionHeader()
        contentType(ContentType.Application.Json)
        setBody(distraction)
    }
    return AppClient.getOrRedirect(response)
}

suspend fun Distraction.Companion.delete(distraction: Distraction) {
    val response = AppClient.http.delete("$URL_PATH/${distraction.id}"){
        addSessionHeader()
    }
    AppClient.doOrRedirect(response)
}