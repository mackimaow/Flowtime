package data

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class FlowSession(
    val start: Instant,
    val end: Instant,
    val isBreak: Boolean,
    val breakTimeStartAmount: Int,
    val breakTimeEndAmount: Int,
    val distractions: List<Distraction>,
    override val id: Int? = null,
): DataWithId {
    companion object {
        const val URL_PATH = "flow-session"
    }
}