package database.tables


import database.EntryToDataIso
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction

object UserSessionTable : IntIdTable() {
    val sessionId = varchar("session-id", data.UserSession.ID_LENGTH)
}

class UserSession(id: EntityID<Int>) : IntEntity(id) {
    companion object: EntryToDataIso<UserSession, data.UserSession>(UserSessionTable) {
        override val makeEntry: UserSession.(data.UserSession) -> Unit = {
            sessionId = it.sessionId
        }
        override fun makeData(obj: UserSession): data.UserSession =
            data.UserSession(
                obj.sessionId,
                obj.id.value,
            )

        fun clearSessions() {
            transaction {
                UserSessionTable.deleteAll()
            }
        }
    }
    var sessionId by UserSessionTable.sessionId
}
