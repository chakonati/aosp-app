package dev.superboring.aosp.chakonati.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.superboring.aosp.chakonati.persistence.dao.*
import dev.superboring.aosp.chakonati.persistence.entities.*

@Database(
    entities = [
        MySetup::class,
        LocalPreKey::class,
        LocalSignedPreKey::class,
        RemoteIdentityKey::class,
        RemoteAddress::class,
        SignalSession::class,
        Chat::class,
    ], version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun mySetup(): MySetupDao
    abstract fun localPreKeys(): LocalPreKeyDao
    abstract fun localSignedPreKeys(): LocalSignedPreKeyDao
    abstract fun remoteIdentityKeys(): RemoteIdentityKeyDao
    abstract fun remoteAddresses(): RemoteAddressDao
    abstract fun signalSessions(): SignalSessionDao
    abstract fun chats(): ChatDao
}

lateinit var db: AppDatabase

