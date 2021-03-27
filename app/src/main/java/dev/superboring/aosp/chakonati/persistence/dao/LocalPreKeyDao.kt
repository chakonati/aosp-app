package dev.superboring.aosp.chakonati.persistence.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import dev.superboring.aosp.chakonati.persistence.db
import dev.superboring.aosp.chakonati.persistence.entities.LocalPreKey
import dev.superboring.aosp.chakonati.signal.OneTimePreKeyRefresh
import java.security.SecureRandom

@Dao
interface LocalPreKeyDao {

    @Query("select * from local_pre_keys")
    fun all(): Array<LocalPreKey>

    @Query("select count(id) as count from local_pre_keys")
    fun count(): Int

    @Query("select * from local_pre_keys where key_id = :keyId")
    fun byPreKeyId(keyId: Int): LocalPreKey

    @Query("select * from local_pre_keys ORDER BY id DESC LIMIT 1")
    fun lastKey(): LocalPreKey

    @Insert
    infix fun insert(key: LocalPreKey)

    @Insert
    infix fun insertAll(keys: List<LocalPreKey>)

    @Delete
    infix fun deleteNoEvents(key: LocalPreKey)

    @Query("delete from local_pre_keys where key_id = :keyId")
    infix fun deleteByPreKeyId(keyId: Int)

    @Query("select count(id) from local_pre_keys where key_id = :keyId")
    infix fun hasKey(keyId: Int): Boolean

}

private val random = SecureRandom()

fun LocalPreKeyDao.generateNewKeyId(): Int {
    var keyId: Int
    do {
        keyId = random.nextInt(kotlin.Short.MAX_VALUE.toInt())
    } while (hasKey(keyId))
    return keyId
}

suspend infix fun LocalPreKeyDao.delete(key: LocalPreKey) {
    deleteNoEvents(key)
    OneTimePreKeyRefresh.refreshOneTimePreKeys()
}

val relayServerPassword get() = db.mySetup().get().relayServerPassword