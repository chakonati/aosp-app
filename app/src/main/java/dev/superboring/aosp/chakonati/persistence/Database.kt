package dev.superboring.aosp.chakonati.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.superboring.aosp.chakonati.persistence.dao.LocalPreKeyDao
import dev.superboring.aosp.chakonati.persistence.dao.LocalSignedPreKeyDao
import dev.superboring.aosp.chakonati.persistence.dao.MySetupDao
import dev.superboring.aosp.chakonati.persistence.entities.LocalPreKey
import dev.superboring.aosp.chakonati.persistence.entities.LocalSignedPreKey
import dev.superboring.aosp.chakonati.persistence.entities.MySetup

@Database(
    entities = [
        MySetup::class,
        LocalPreKey::class,
        LocalSignedPreKey::class,
    ], version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun mySetup(): MySetupDao
    abstract fun localPreKeys(): LocalPreKeyDao
    abstract fun localSignedPreKeys(): LocalSignedPreKeyDao
}

lateinit var db: AppDatabase

