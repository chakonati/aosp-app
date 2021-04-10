package dev.superboring.aosp.chakonati.persistence.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import dev.superboring.aosp.chakonati.persistence.entities.LocalSignedPreKey

@Dao
interface LocalSignedPreKeyDao {

    @Query("select * from local_signed_pre_keys")
    fun all(): Array<LocalSignedPreKey>

    @Query("select * from local_signed_pre_keys where id = :id")
    fun byPreKeyId(id: Int): LocalSignedPreKey

    @Insert
    infix fun insert(key: LocalSignedPreKey)

    @Delete
    infix fun delete(key: LocalSignedPreKey)

    @Query("delete from local_signed_pre_keys where id = :id")
    infix fun deleteByPreKeyId(id: Int)

    @Query("select count(id) from local_signed_pre_keys where id = :id")
    infix fun hasKey(id: Int): Boolean

}
