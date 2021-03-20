package dev.superboring.aosp.chakonati.persistence.dao

import androidx.room.*
import androidx.room.Dao
import dev.superboring.aosp.chakonati.persistence.db
import dev.superboring.aosp.chakonati.persistence.entities.RemoteAddress
import dev.superboring.aosp.chakonati.persistence.entities.SignalSession

@Dao
interface SignalSessionDao {

    @Query("select * from signal_sessions")
    fun all(): Array<SignalSession>

    @Insert
    infix fun insert(session: SignalSession)

    @Delete
    infix fun delete(session: SignalSession)

    @Query(
        """
        select count(s.id) from signal_sessions s
        inner join remote_addresses ra on s.remote_address_id = ra.id
        where ra.address = :withAddress and ra.device_id = :withDeviceId
        """
    )
    fun hasSession(withDeviceId: Int, withAddress: String): Boolean

    @Query(
        """
        select s.* from signal_sessions s
        inner join remote_addresses ra on s.remote_address_id = ra.id
        where ra.address = :address and ra.device_id = :deviceId
        """
    )
    fun get(deviceId: Int, address: String): SignalSession

    @Query(
        """
        select s.* from signal_sessions s
        inner join remote_addresses ra on s.remote_address_id = ra.id
        where ra.address = :address
        """
    )
    fun get(address: String): List<SignalSession>

    @Query(
        """
        select ra.device_id from signal_sessions s
        inner join remote_addresses ra on s.remote_address_id = ra.id
        where ra.address = :address
        """
    )
    fun getDeviceIds(address: String): List<Int>
}

fun SignalSessionDao.insertWithAddress(session: SignalSession, address: RemoteAddress) {
    if (address.Id <= 0) {
        throw InvalidSessionWithAddressInsertion("address ID is not > 0. Fetch it first.")
    }
    session.remoteAddressId = address.Id
    db.signalSessions() insert session
}

infix fun SignalSessionDao.deleteByRemoteAddress(address: RemoteAddress) {
    db.signalSessions().run { delete(getByAddress(address)) }
}

suspend infix fun SignalSessionDao.deleteByAddressName(address: String) {
    db.withTransaction {
        db.signalSessions().run {
            get(address).forEach { delete(it) }
        }
    }
}

infix fun SignalSessionDao.getByAddress(address: RemoteAddress) =
    get(address.deviceId, address.address)

infix fun SignalSessionDao.hasSession(address: RemoteAddress) =
    hasSession(address.deviceId, address.address)

class InvalidSessionWithAddressInsertion(details: String) :
    RuntimeException("cannot do invalid insertion with address: $details")
