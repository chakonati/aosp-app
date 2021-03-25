package dev.superboring.aosp.chakonati.persistence.dao

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import dev.superboring.aosp.chakonati.persistence.entities.Chat

@Dao
interface ChatDao {

    @Query("select * from chats")
    fun all(): DataSource.Factory<Int, Chat>

    @Insert
    infix fun insert(chat: Chat)

    @Delete
    infix fun delete(chat: Chat)

    @Query("delete from chats where id = :id")
    infix fun delete(id: Int)

}