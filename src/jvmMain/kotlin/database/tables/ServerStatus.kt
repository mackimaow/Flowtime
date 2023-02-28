package database.tables

import database.AppDatabase
import database.EntryToDataIso
import database.LocalDateTimeIso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.transactions.transaction

object ServerStatusTable : IntIdTable() {
    val lastKeepLiveTime = datetime("last-keep-live-time")
    val mainSession = optReference("main-session", UserSessionTable)
}

class ServerStatus(id: EntityID<Int>) : IntEntity(id) {
    companion object: EntryToDataIso<ServerStatus, data.ServerStatus>(ServerStatusTable) {
        var shutdownServer = false

        override val makeEntry: ServerStatus.(data.ServerStatus) -> Unit = {
            lastKeepLiveTime = LocalDateTimeIso.morphInv(it.lastKeepLiveTime)
            mainSession = it.mainSession?.let { session -> UserSession.morphInv(session) }
        }
        override fun makeData(obj: ServerStatus): data.ServerStatus =
            data.ServerStatus(
                LocalDateTimeIso.morph(obj.lastKeepLiveTime),
                obj.mainSession?.let{ session -> UserSession.morph(session) },
                obj.id.value,
            )

        val current: ServerStatus?
            get() = ServerStatus.all().firstOrNull()

        fun removeMainSession() {
            transaction {
                current?.also {
                    it.mainSession = null
                }
            }
        }
        fun manageLoop() {
            runBlocking {
                withContext(Dispatchers.IO) {
                    while (!shutdownServer) {
                        AppDatabase.transaction {
                            current?.let {
                                val dataStatus = ServerStatus.morph(it)
                                val serverStatus = dataStatus.copy(
                                    lastKeepLiveTime = Clock.System.now()
                                )
                                ServerStatus.morphInv(serverStatus)  // update session
                            } ?: run {
                                val dataSession = data.ServerStatus(
                                    Clock.System.now()
                                )
                                ServerStatus.morphInv(dataSession) // add session
                            }
                        }
                        delay(10000) // delay every 10 seconds
                    }
                }
            }
        }
    }
    var lastKeepLiveTime by ServerStatusTable.lastKeepLiveTime
    var mainSession by UserSession optionalReferencedOn ServerStatusTable.mainSession
}


