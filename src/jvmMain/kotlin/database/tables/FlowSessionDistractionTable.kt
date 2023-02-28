package database.tables

import org.jetbrains.exposed.sql.Table


object FlowSessionDistractionTable : Table() {
    val flowSession = reference("flow-session", FlowSessionTable)
    val distraction = reference("distraction", DistractionTable)
    override val primaryKey = PrimaryKey(
        flowSession,
        distraction,
        name = "PK-flow-session-distraction"
    )
}