package data

import database.AppDatabase
import database.tables.current
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import withSession

val UserData.Companion.route: Route.() -> Unit
    get() = {
        route(URL_PATH) {
            get {
                withSession {
                    val userData = AppDatabase.transaction {
                        val userData = database.tables.UserData.all().firstOrNull()
                        if (userData == null)
                            database.tables.UserData.morph(database.tables.UserData.new {})
                        else
                            database.tables.UserData.morph(userData)
                    }
                    call.respond(userData)
                }
            }
            post {
                withSession {
                    val newUserData = call.receive<UserData>()
                    if (newUserData.id != null) {
                        AppDatabase.transaction {
                            if (newUserData.timersOn) {
                                val oldUserData = database.tables.UserData.morph(
                                    database.tables.UserData[newUserData.id]
                                )
                                val newFlowSession = FlowSession.createFromUserData(
                                    oldUserData,
                                    newUserData
                                )
                                newFlowSession?. also {
                                    database.tables.FlowSession.morphInv(it)  // add flow session to db
                                }
                            }
                            database.tables.UserData.morphInv(newUserData)
                        }
                        call.respond(HttpStatusCode.OK)
                    } else {
                        call.respond(HttpStatusCode.BadRequest)
                    }
                }
            }
        }
    }