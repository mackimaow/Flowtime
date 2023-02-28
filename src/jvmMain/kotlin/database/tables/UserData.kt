package database.tables

import data.FlowSession
import data.createFromUserData
import database.EntryToDataIso
import database.LocalDateTimeIso
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.transactions.transaction

object UserDataTable : IntIdTable() {
    val workTimerEnabled = bool("work-timer-enabled").default(false) // otherwise break timer is enabled if timersOn
    val timersOn = bool("timers-on").default(false)
    val timerStart = datetime("timer-start").nullable()
    val startBreakSeconds = integer("break-seconds").default(0)
    val endBreakSeconds = integer("stop-break-seconds").default(0)
}

class UserData(id: EntityID<Int>) : IntEntity(id) {
    companion object: EntryToDataIso<UserData, data.UserData>(UserDataTable) {
        override val makeEntry: UserData.(data.UserData) -> Unit
            get() = {
                workTimerEnabled = it.workTimerEnabled
                timersOn = it.timersOn
                timerStart = it.timerStart?.let { inst ->
                    LocalDateTimeIso.morphInv(inst)
                }
                startBreakSeconds = it.startBreakSeconds
                endBreakSeconds = it.endBreakSeconds
            }

        override fun makeData(obj: UserData): data.UserData {
            return data.UserData(
                workTimerEnabled = obj.workTimerEnabled,
                timersOn = obj.timersOn,
                timerStart = obj.timerStart?.let {
                    LocalDateTimeIso.morph(it)
                },
                startBreakSeconds = obj.startBreakSeconds,
                endBreakSeconds = obj.endBreakSeconds,
                obj.id.value,
            )
        }
    }
    var workTimerEnabled by UserDataTable.workTimerEnabled
    var timersOn by UserDataTable.timersOn
    var timerStart by UserDataTable.timerStart
    var startBreakSeconds by UserDataTable.startBreakSeconds
    var endBreakSeconds by UserDataTable.endBreakSeconds
}

val UserData.Companion.current: UserData?
    get() = UserData.all().firstOrNull()

fun UserData.Companion.repairIfSuddenExit() {
    transaction {
        ServerStatus.current?.also { status ->
            UserData.current?.also { oldUserDataEntry ->
                if (oldUserDataEntry.timersOn && oldUserDataEntry.workTimerEnabled) {
                    val oldUserData = morph(oldUserDataEntry)

                    val userSettings = UserSettings.current?.let {
                        UserSettings.morph(it)
                    } ?: data.UserSettings()

                    val workBreakRatio = userSettings.workToBreakRatio
                    val previousBaseSeconds = oldUserData.startBreakSeconds
                    val previousStartTime = oldUserData.timerStart
                    val workTimeEnd = LocalDateTimeIso.morph(status.lastKeepLiveTime)
                    val totalWorkTime = previousStartTime?.let {
                        (workTimeEnd - it).inWholeSeconds
                    } ?: 0L
                    val newBaseSeconds = previousBaseSeconds + totalWorkTime / workBreakRatio

                    val newUserData = oldUserData.copy(
                        workTimerEnabled = false,
                        timerStart = workTimeEnd,
                        startBreakSeconds = newBaseSeconds.toInt(),
                        id = oldUserDataEntry.id.value
                    )

                    val flowSession = FlowSession.createFromUserData(
                        oldUserData.copy(
                            endBreakSeconds = newBaseSeconds.toInt()
                        ),
                        newUserData
                    )

                    flowSession?.also {
                        database.tables.FlowSession.morphInv(it)  // add flowSession to DB
                    }

                    morphInv(newUserData) // update entry in DB
                }
            }
        }
    }
}