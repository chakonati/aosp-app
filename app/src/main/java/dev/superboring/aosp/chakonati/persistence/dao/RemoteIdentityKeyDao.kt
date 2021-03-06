package dev.superboring.aosp.chakonati.persistence.dao

import androidx.room.*
import androidx.room.Dao
import dev.superboring.aosp.chakonati.persistence.entities.RemoteIdentityKey

@Dao
interface RemoteIdentityKeyDao {

    @Query("select * from remote_identity_keys")
    fun all(): Array<RemoteIdentityKey>

    @Query("select * from remote_identity_keys where id = :id")
    infix fun get(id: Int): RemoteIdentityKey

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    infix fun insert(key: RemoteIdentityKey): Long

    @Delete
    infix fun delete(key: RemoteIdentityKey)

}
