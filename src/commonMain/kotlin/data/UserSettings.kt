package data

import kotlinx.serialization.Serializable

@Serializable
data class UserSettings (
    val workToBreakRatio: Int = DEFAULT_RATIO,
    override val id: Int? = null
): DataWithId {
    companion object {
        const val URL_PATH = "user-settings"
        const val DEFAULT_RATIO = 4
    }
}