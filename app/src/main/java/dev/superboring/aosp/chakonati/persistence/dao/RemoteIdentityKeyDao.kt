package dev.superboring.aosp.chakonati.persistence.dao

import androidx.room.*
import androidx.room.Dao
import dev.superboring.aosp.chakonati.persistence.entities.LocalPreKey
import dev.superboring.aosp.chakonati.persistence.entities.RemoteAddress
import dev.superboring.aosp.chakonati.persistence.entities.RemoteAddressAndIdentityKey
import dev.superboring.aosp.chakonati.persistence.entities.RemoteIdentityKey

@Dao
interface RemoteIdentityKeyDao {

    @Query("select * from remote_identity_keys")
    fun all(): Array<RemoteIdentityKey>

    @Insert
    infix fun insert(key: RemoteIdentityKey)

    @Delete
    infix fun delete(key: RemoteIdentityKey)

}