package database

import category.Isomorphism
import data.DataWithId
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.IdTable

abstract class EntryToDataIso<E: IntEntity, D: DataWithId>(table: IdTable<Int>):
    IntEntityClass<E>(table),
    Isomorphism<E, D>
{
    abstract val makeEntry: (E.(D) -> Unit)
    abstract fun makeData(obj: E): D

    override fun morph(obj: E): D {
        return makeData(obj)
    }

    override fun morphInv(obj: D): E {
        val entryObj: E
        val id = obj.id
        if (id == null) {
            entryObj =
                this.new {
                    makeEntry(obj)
                }
        } else {
            val previousUserSession = this.findById(id)
            if (previousUserSession == null) {
                entryObj =
                    this.new (id) {
                        makeEntry(obj)
                    }
            } else {
                entryObj = previousUserSession
                entryObj.makeEntry(obj)
            }
        }
        return entryObj
    }
}