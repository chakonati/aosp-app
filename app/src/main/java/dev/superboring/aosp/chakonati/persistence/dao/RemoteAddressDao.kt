package dev.superboring.aosp.chakonati.persistence.dao

import androidx.room.*
import androidx.room.Dao
import dev.superboring.aosp.chakonati.persistence.db
import dev.superboring.aosp.chakonati.persistence.entities.RemoteAddress
import dev.superboring.aosp.chakonati.persistence.entities.RemoteAddressAndIdentityKey

@Dao
interface RemoteAddressDao {

    @Query("select * from remote_addresses")
    fun all(): Array<RemoteAddress>

    @Query(
        """
            select count(id) from remote_addresses
            where device_id = :deviceId and address = :address
            """
    )
    fun exists(deviceId: Int, address: String): Boolean

    @Transaction
    @Query(
        """
            select * from remote_addresses
            where device_id = :deviceId and address = :address
            """
    )
    fun get(deviceId: Int, address: String): RemoteAddressAndIdentityKey

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    infix fun insert(address: RemoteAddress)

    @Delete
    infix fun delete(address: RemoteAddress)

    @Transaction
    @Query("select * from remote_addresses")
    fun allWithIdentityKeys(): List<RemoteAddressAndIdentityKey>

}


suspend infix fun RemoteAddressDao.insertWithIdentityKey(
    addressWithKey: RemoteAddressAndIdentityKey
) {
    db.run {
        withTransaction {
            remoteIdentityKeys() insert addressWithKey.identityKey
            remoteAddresses() insert addressWithKey.address
        }
    }
}