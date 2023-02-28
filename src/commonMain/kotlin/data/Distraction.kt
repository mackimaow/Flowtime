package data

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import platformDependant.DataGridRowData
@Serializable
data class Distraction(
    val tag: String,
    val tagColor: String,
    val description: String,
    val created: Instant,
    override val id: Int? = null,
): DataWithId, DataGridRowData {
    companion object {
        const val URL_PATH = "distraction"
    }
}