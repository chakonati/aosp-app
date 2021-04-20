package dev.superboring.aosp.chakonati.persistence.dao

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import dev.superboring.aosp.chakonati.persistence.dbTx
import dev.superboring.aosp.chakonati.persistence.entities.DBMessage
import org.jetbrains.annotations.TestOnly
import kotlin.collections.set

var lastUpdatedChatId by mutableStateOf(0)
var messageUpdateTracker = hashMapOf<Int, Long>()

@Dao
interface MessageDao {

    @Query("select * from messages")
    fun all(): DataSource.Factory<Int, DBMessage>

    @Query("select * from messages where chat_id = :chatId")
    fun all(chatId: Int): DataSource.Factory<Int, DBMessage>

    @TestOnly
    @Query("select * from messages where chat_id = :chatId")
    fun allFull(chatId: Int): List<DBMessage>

    @Query(
        """
        select * from messages where chat_id = :chatId order by id desc limit :limit
        """
    )
    fun last(chatId: Int, limit: Int): List<DBMessage>

    @Insert
    infix fun insert(message: DBMessage)

    @Delete
    infix fun delete(message: DBMessage)

    @Query("delete from messages where id = :id")
    infix fun delete(id: Int)

}

suspend infix fun MessageDao.add(message: DBMessage) {
    dbTx {
        insert(message)
        messageUpdateTracker[message.chatId] = (messageUpdateTracker[message.chatId] ?: 0) + 1
        lastUpdatedChatId = message.chatId
    }
}