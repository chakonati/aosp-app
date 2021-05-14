package dev.superboring.aosp.chakonati.persistence.dao

import androidx.room.*
import androidx.room.Dao
import dev.superboring.aosp.chakonati.persistence.db
import dev.superboring.aosp.chakonati.persistence.entities.RemoteAddress
import dev.superboring.aosp.chakonati.persistence.entities.RemoteIdentityKey

@Dao
interface RemoteAddressDao {

    @Query("select * from remote_addresses")
    fun all(): Array<RemoteAddress>

    @Query("select count(id) from remote_addresses where address = :address")
    fun exists(address: String): Boolean

    @Transaction
    @Query("select * from remote_addresses where address = :address")
    fun get(address: String): RemoteAddress

    @Query("select * from remote_addresses where id = :id")
    fun getAddress(id: Int): RemoteAddress

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    infix fun insert(address: RemoteAddress): Long

    @Delete
    infix fun delete(address: RemoteAddress)
}


suspend fun RemoteAddressDao.upsertWithIdentityKey(
    address: RemoteAddress,
    identityKey: RemoteIdentityKey,
) {
    db.run {
        if (!exists(address.address)) {
            withTransaction {
                address.identityKeyId = remoteIdentityKeys().insert(identityKey).toInt()
                remoteAddresses() insert address
            }
        } else {
            // TODO: handle update
        }
    }
}

class MissingIdentityKey : RuntimeException("the identity key is missing in the RemoteAddress")
