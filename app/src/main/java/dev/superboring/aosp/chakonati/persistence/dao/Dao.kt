package dev.superboring.aosp.chakonati.persistence.dao

import android.util.Log
import androidx.room.withTransaction
import dev.superboring.aosp.chakonati.extensions.t.tag
import dev.superboring.aosp.chakonati.persistence.db
import dev.superboring.aosp.chakonati.x.debug
import dev.superboring.aosp.chakonati.x.logging.logDebug

interface DaoBase

interface Dao<T> : DaoBase

interface SingleEntryDao<T> : Dao<T> {
    val defaultValue: T

    fun count(): Int
    fun insert(element: T)
    fun update(element: T)

    fun getValue(): T
}

suspend infix fun <T> SingleEntryDao<T>.save(element: T) {
    db.withTransaction {
        logDebug("Saving %s", element.toString())
        when (count()) {
            0 -> insert(element)
            else -> update(element)
        }
    }
}

inline fun <reified T> SingleEntryDao<T>.get() =
    when (count()) {
        0 -> defaultValue
        else -> getValue()
    }.apply {
        logDebug("Got %s", toString())
    }
