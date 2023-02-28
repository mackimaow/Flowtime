package data

import database.AppDatabase
import database.tables.FlowSessionDistractionTable
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import withSession
import kotlin.text.get


val Distraction.Companion.route: (Route.() -> Unit)
    get() = {
        route(URL_PATH) {
            get {
                withSession {
                    val dataDistractions = AppDatabase.transaction {
                        database.tables.Distraction.all().map {
                            database.tables.Distraction.morph(it)
                        }.toList()
                    }
                    call.respond(dataDistractions)
                }
            }
            get("/{id}") {
                withSession {
                    val id = call.parameters["id"]?.toInt() ?: error("Invalid get request")
                    val dataDistraction = AppDatabase.transaction {
                        val distraction = database.tables.Distraction.findById(id)
                            ?: error("No distraction with id '$id' exists")
                        database.tables.Distraction.morph(distraction)
                    }
                    call.respond(dataDistraction)
                }
            }
            post {
                withSession {
                    val distractionData = call.receive<Distraction>()

                    val newDistractionData = AppDatabase.transaction {
                        val newDistraction = database.tables.Distraction.morphInv(distractionData)
                        database.tables.Distraction.morph(newDistraction)
                    }
                    call.respond(newDistractionData)
                }
            }
            delete("/{id}") {
                withSession {
                    val id = call.parameters["id"]?.toInt() ?: error("Invalid delete request")
                    AppDatabase.transaction {
                        FlowSessionDistractionTable.deleteWhere {
                            distraction eq id
                        }
                        database.tables.Distraction.findById(id)?.delete()
                    }
                    call.respond(HttpStatusCode.OK)
                }
            }
        }
    }

