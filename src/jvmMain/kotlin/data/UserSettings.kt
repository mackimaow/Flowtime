package data

import database.AppDatabase
import database.tables.FlowSession
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import withSession


val UserSettings.Companion.route: Route.() -> Unit
    get() = {
        route(URL_PATH) {
            delete {
                withSession {
                    AppDatabase.reset()
                    call.respond(HttpStatusCode.OK)
                }
            }
            get {
                withSession {
                    val settings = AppDatabase.transaction {
                        val settings = database.tables.UserSettings.all().firstOrNull()
                        if (settings == null)
                            database.tables.UserSettings.morph(database.tables.UserSettings.new {})
                        else
                            database.tables.UserSettings.morph(settings)
                    }
                    call.respond(settings)
                }
            }
            post {
                withSession {
                    val settings = call.receive<UserSettings>()
                    if (settings.id != null) {
                        AppDatabase.transaction {
                            database.tables.UserSettings.morphInv(settings)
                        }
                        call.respond(HttpStatusCode.OK)
                    } else {
                        call.respond(HttpStatusCode.BadRequest)
                    }
                }
            }
        }
    }