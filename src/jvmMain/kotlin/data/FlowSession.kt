package data

import database.AppDatabase
import database.tables.FlowSessionTable
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import withSession


val FlowSession.Companion.route: Route.() -> Unit
    get() = {
        route(URL_PATH) {
            route("latest") {
                get("/{amt}") {
                    withSession {
                        val amt = call.parameters["amt"]?.toInt() ?: error("Invalid get request")
                        val sessions = AppDatabase.transaction {
                            FlowSessionTable
                                .selectAll()
                                .orderBy(FlowSessionTable.end, SortOrder.DESC)
                                .take(amt).map {
                                    database.tables.FlowSession.morph(
                                        database.tables.FlowSession.wrapRow(it)
                                    )
                                }.toList()
                        }
                        call.respond(sessions)
                    }
                }
            }
            get {
                withSession {
                    val dataFlowSessions = AppDatabase.transaction {
                        database.tables.FlowSession.all().map {
                            database.tables.FlowSession.morph(it)
                        }.toList()
                    }
                    call.respond(dataFlowSessions)
                }
            }
            get("/{id}") {
                withSession {
                    val id = call.parameters["id"]?.toInt() ?: error("Invalid get request")
                    val dataFlowSession = AppDatabase.transaction {
                        val flowSession = database.tables.FlowSession.findById(id)
                            ?: error("No flow session with id '$id' exists")
                        database.tables.FlowSession.morph(flowSession)
                    }
                    call.respond(dataFlowSession)
                }
            }
            post {
                withSession {
                    val flowSessionData = call.receive<FlowSession>()
                    AppDatabase.transaction {
                        database.tables.FlowSession.morphInv(flowSessionData)
                    }
                    call.respond(HttpStatusCode.OK)
                }
            }
            delete("/{id}") {
                withSession {
                    val id = call.parameters["id"]?.toInt() ?: error("Invalid delete request")
                    AppDatabase.transaction {
                        database.tables.FlowSession.findById(id)?.delete()
                    }
                    call.respond(HttpStatusCode.OK)
                }
            }
        }
    }

fun FlowSession.Companion.createFromUserData(
    oldUserData: UserData,
    newUserData: UserData
): FlowSession? {
    if (oldUserData.timersOn) {
        if (
            oldUserData.workTimerEnabled &&
            !newUserData.workTimerEnabled
        ) {
            // if work session

            return FlowSession (
                oldUserData.timerStart!!,
                newUserData.timerStart!!,
                false,
                oldUserData.startBreakSeconds,
                oldUserData.endBreakSeconds,
                listOf()
            )

        } else if (
            !oldUserData.workTimerEnabled &&
            newUserData.workTimerEnabled
        ) {
            // else if break session

            return FlowSession (
                oldUserData.timerStart!!,
                newUserData.timerStart!!,
                true,
                oldUserData.startBreakSeconds,
                oldUserData.endBreakSeconds,
                listOf()
            )
        }
    }
    return null
}
