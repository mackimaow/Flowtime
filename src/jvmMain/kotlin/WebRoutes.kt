import data.FlowTimeEventStatistics
import data.route
import database.AppDatabase
import database.tables.ServerStatus
import database.tables.UserSession
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.util.pipeline.*
import io.ktor.server.application.*
import io.ktor.server.plugins.compression.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.response.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

object WebRoutes {
    fun createAndStartServer() {
        embeddedServer(Netty, 9090) {
            install(Sessions) {
                header<data.UserSession>(data.UserSession.URL_PATH)
            }
            install(ContentNegotiation) {
                json()
            }
            install(CORS) {
                allowMethod(HttpMethod.Get)
                allowMethod(HttpMethod.Post)
                allowMethod(HttpMethod.Delete)
                allowHeader(data.UserData.URL_PATH)
                exposeHeader(data.UserData.URL_PATH)
                hostIsIp("localhost") // only allow localhost for resources
            }
            install(Compression) {
                gzip()
            }
            routing {
                get("/") {
                    call.respondText(
                        this::class.java.classLoader.getResource("index.html")!!.readText(),
                        ContentType.Text.Html
                    )
                }

                // this is to serve html/javascript/ccs files
                static("/") {
                    // gradle puts all html/javascript/css files in one folder
                    // when it builds the project
                    resources("")
                }
                from(data.Distraction.route)
                from(data.FlowSession.route)
                from(data.UserSettings.route)
                from(data.UserData.route)
                from(data.UserSession.route)
                from(FlowTimeEventStatistics.route)
            }
        }.start(wait = false)  // non-blocking
    }
}


inline fun Route.from(route: Route.() -> Unit) {
    route()
}


suspend inline fun PipelineContext<Unit, ApplicationCall>.withSession(
    body: (PipelineContext<Unit, ApplicationCall>.(data.UserSession) -> Unit)
) {
    val session = call.sessions.get<data.UserSession>()
    if (session == null) {
        call.respond(HttpStatusCode.Forbidden)
    } else {
        val isMainSession = AppDatabase.transaction {
            ServerStatus.current?.let { status ->
                status.mainSession?.let{ UserSession.morph(it) } == session
            } ?: false
        }
        if (isMainSession)
            body(session)
        else
            call.respond(HttpStatusCode.Forbidden)
    }
}

suspend fun PipelineContext<Unit, ApplicationCall>.setSession(dataSession: data.UserSession): data.UserSession {
    val sessionWithId = AppDatabase.transaction {
        val session = UserSession.morphInv(
            dataSession
        ) // new session
        ServerStatus.current?.let { status ->
            status.mainSession = session
        }
        UserSession.morph(session)
    }
    call.sessions.set(sessionWithId)
    return sessionWithId
}