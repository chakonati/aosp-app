package dev.superboring.aosp.chakonati.persistence.dao

import androidx.room.withTransaction
import dev.superboring.aosp.chakonati.persistence.db

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
        when (count()) {
            0 -> insert(element)
            else -> update(element)
        }
    }
}

fun <T> SingleEntryDao<T>.get() =
    when (count()) {
        0 -> defaultValue
        else -> getValue()
    }
