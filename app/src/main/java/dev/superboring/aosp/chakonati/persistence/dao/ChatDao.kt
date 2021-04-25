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

    @Query("select * from chats")
    fun allFull(): List<Chat>

    @Query("select * from chats where id = :id")
    infix fun get(id: Long): Chat

    @Query("select * from chats where remote_address_id = :remoteAddressId")
    infix fun getByRemoteAddressId(remoteAddressId: Int): Chat

    @Insert
    infix fun insert(chat: Chat): Long

    @Delete
    infix fun delete(chat: Chat)

    @Query("delete from chats where id = :id")
    infix fun delete(id: Int)

}
