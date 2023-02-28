package data

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import setSession


val UserSession.Companion.route: Route.() -> Unit
    get() = {
        route(URL_PATH) {
            post {
                val session = call.receive<UserSession>()
                val sessionWithId = setSession(session)
                call.respond(sessionWithId)
            }
            post("/shutdown") {
                call.respond(HttpStatusCode.OK)
                database.tables.ServerStatus.shutdownServer = true
            }
        }
    }