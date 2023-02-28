package database.tables

import category.Isomorphism
import database.EntryToDataIso
import database.LocalDateTimeIso
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.SizedCollection
import org.jetbrains.exposed.sql.SizedIterable
import org.jetbrains.exposed.sql.javatime.datetime

object DistractionTable : IntIdTable() {
    val tag = varchar("tag", 50)
    val tagColor = varchar("tag-color", 9)
    val description = text("description")
    val created = datetime("created")
}

object DistractionListIso: Isomorphism<SizedIterable<Distraction>, List<data.Distraction>> {
    override fun morph(obj: SizedIterable<Distraction>): List<data.Distraction> {
        return obj.map {
            Distraction.morph(it)
        }.toList()
    }
    override fun morphInv(obj: List<data.Distraction>): SizedIterable<Distraction> {
        return SizedCollection(
            obj.map {
                Distraction.morphInv(it)
            }.toList()
        )
    }
}

class Distraction(id: EntityID<Int>) : IntEntity(id) {
    companion object: EntryToDataIso<Distraction, data.Distraction>(DistractionTable) {
        override val makeEntry: Distraction.(data.Distraction) -> Unit
            get() = {
                description = it.description
                created = LocalDateTimeIso.morphInv(it.created)
                tag = it.tag
                tagColor = it.tagColor
            }

        override fun makeData(obj: Distraction): data.Distraction {
            return data.Distraction(
                obj.tag,
                obj.tagColor,
                obj.description,
                LocalDateTimeIso.morph(obj.created),
                obj.id.value
            )
        }
    }
    var tag by DistractionTable.tag
    var description by DistractionTable.description
    var created by DistractionTable.created
    var tagColor by DistractionTable.tagColor
}