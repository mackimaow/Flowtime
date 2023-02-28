package data

import kotlinx.serialization.Serializable

@Serializable
data class UserSession(
    val sessionId: String = generateId(),
    override val id: Int? = null
): DataWithId {
    companion object {
        const val URL_PATH = "user-session"
        const val ID_LENGTH = 15
        fun generateId(): String {
            val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
            return (1..ID_LENGTH)
                .map { allowedChars.random() }
                .joinToString("")
        }
    }
}