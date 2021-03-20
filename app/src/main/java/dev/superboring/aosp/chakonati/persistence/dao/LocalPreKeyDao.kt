package dev.superboring.aosp.chakonati.persistence.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import dev.superboring.aosp.chakonati.persistence.entities.LocalPreKey

@Dao
interface LocalPreKeyDao {

    @Query("select * from local_pre_keys")
    fun all(): Array<LocalPreKey>

    @Query("select * from local_pre_keys where id = :id")
    fun byPreKeyId(id: Int): LocalPreKey

    @Insert
    fun insert(key: LocalPreKey)

    @Delete
    fun delete(key: LocalPreKey)

    @Query("delete from local_pre_keys where id = :id")
    fun deleteByPreKeyId(id: Int)

    @Query("select count(id) from local_pre_keys where id = :id")
    infix fun hasKey(id: Int): Boolean

}