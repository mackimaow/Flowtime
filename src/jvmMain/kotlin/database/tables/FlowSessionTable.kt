package database.tables

import database.EntryToDataIso
import database.LocalDateTimeIso
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.datetime

object FlowSessionTable : IntIdTable() {
    val start = datetime("start")
    val end = datetime("end")
    val isBreak = bool("is-break")
    val breakTimeStartAmount = integer("break-time-start-amount")
    val breakTimeEndAmount = integer("break-time-end-amount")
}

class FlowSession(id: EntityID<Int>) : IntEntity(id) {
    companion object: EntryToDataIso<FlowSession, data.FlowSession>(FlowSessionTable) {
        override val makeEntry: FlowSession.(data.FlowSession) -> Unit
            get() = {
                start = LocalDateTimeIso.morphInv(it.start)
                end = LocalDateTimeIso.morphInv(it.end)
                isBreak = it.isBreak
                breakTimeStartAmount = it.breakTimeStartAmount
                breakTimeEndAmount = it.breakTimeEndAmount
                distractions = DistractionListIso.morphInv(it.distractions)
            }

        override fun makeData(obj: FlowSession): data.FlowSession {
            return data.FlowSession(
                LocalDateTimeIso.morph(obj.start),
                LocalDateTimeIso.morph(obj.end),
                obj.isBreak,
                obj.breakTimeStartAmount,
                obj.breakTimeEndAmount,
                DistractionListIso.morph(obj.distractions),
                obj.id.value,
            )
        }
    }

    var start by FlowSessionTable.start
    var end by FlowSessionTable.end
    var distractions by Distraction via FlowSessionDistractionTable
    var breakTimeStartAmount by FlowSessionTable.breakTimeStartAmount
    var breakTimeEndAmount by FlowSessionTable.breakTimeEndAmount
    var isBreak by FlowSessionTable.isBreak
}
