package database.tables

import database.EntryToDataIso
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object UserSettingsTable : IntIdTable() {
    val workToBreakRatio = integer("work-to-break-ratio").default(
        data.UserSettings.DEFAULT_RATIO
    )
}

class UserSettings(id: EntityID<Int>) : IntEntity(id) {
    companion object: EntryToDataIso<UserSettings, data.UserSettings>(UserSettingsTable) {
        override val makeEntry: UserSettings.(data.UserSettings) -> Unit
            get() = {
                workToBreakRatio = it.workToBreakRatio
            }
        override fun makeData(obj: UserSettings): data.UserSettings {
            return data.UserSettings(
                obj.workToBreakRatio,
                obj.id.value,
            )
        }
    }
    var workToBreakRatio by UserSettingsTable.workToBreakRatio
}

val UserSettings.Companion.current: UserSettings?
    get() = UserSettings.all().firstOrNull()