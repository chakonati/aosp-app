package dev.superboring.aosp.chakonati.persistence.dao

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import dev.superboring.aosp.chakonati.persistence.dbTx
import dev.superboring.aosp.chakonati.persistence.entities.DBMessage
import org.jetbrains.annotations.TestOnly

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
    fun last(chatId: Long, limit: Int): LiveData<List<DBMessage>>

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
    }
}