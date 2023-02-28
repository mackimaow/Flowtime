package data

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class UserData (
    // timer related stuff
    val workTimerEnabled: Boolean = false, // otherwise break timer is enabled if timersOn
    val timersOn: Boolean = false,
    val timerStart: Instant? = null,
    val startBreakSeconds: Int = 0,
    val endBreakSeconds: Int = 0,
    override val id: Int? = null
): DataWithId {
    companion object {
        const val URL_PATH = "user-data"
    }
}