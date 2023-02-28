package data

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class ServerStatus(
    val lastKeepLiveTime: Instant,
    val mainSession: UserSession? = null,
    override val id: Int? = null
): DataWithId {
    companion object {
        const val URL_PATH = "server-status"
    }
}