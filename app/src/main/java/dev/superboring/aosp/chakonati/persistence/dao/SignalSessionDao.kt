package dev.superboring.aosp.chakonati.persistence.dao

import androidx.room.*
import androidx.room.Dao
import dev.superboring.aosp.chakonati.persistence.db
import dev.superboring.aosp.chakonati.persistence.entities.RemoteAddress
import dev.superboring.aosp.chakonati.persistence.entities.SignalSession
import dev.superboring.aosp.chakonati.x.logging.logDebug

@Dao
interface SignalSessionDao {

    @Query("select * from signal_sessions")
    fun all(): Array<SignalSession>

    @Insert
    infix fun insert(session: SignalSession)

    @Update
    infix fun update(session: SignalSession)

    @Delete
    infix fun delete(session: SignalSession)

    @Query(
        """
        select count(s.id) from signal_sessions s
        inner join remote_addresses ra on s.remote_address_id = ra.id
        where ra.address = :withAddress
        """
    )
    fun hasSession(withAddress: String): Boolean

    @Query(
        """
        select s.* from signal_sessions s
        inner join remote_addresses ra on s.remote_address_id = ra.id
        where ra.address = :address
        """
    )
    fun get(address: String): SignalSession

    @Query(
        """
        select device_id from signal_sessions s
        inner join remote_addresses ra on s.remote_address_id = ra.id
        where ra.address = :address
        """
    )
    fun getDeviceIds(address: String): List<Int>
}

fun SignalSessionDao.upsertWithAddress(session: SignalSession, address: RemoteAddress) {
    logDebug("Inserting signal session with $address")
    if (address.Id <= 0) {
        throw InvalidSessionWithAddressInsertion("address ID is not > 0. Fetch it first.")
    }
    session.remoteAddressId = address.Id
    if (db.signalSessions().hasSession(address.address)) {
        db.signalSessions() update session
    } else {
        db.signalSessions() insert session
    }
}

infix fun SignalSessionDao.deleteByRemoteAddress(address: RemoteAddress) {
    logDebug("Deleting signal sessions by address $address")
    db.signalSessions().run { delete(getByAddress(address)) }
}

suspend infix fun SignalSessionDao.deleteByAddressName(address: String) {
    logDebug("Deleting signal sessions by address name $address")
    db.withTransaction {
        db.signalSessions().run {
            delete(get(address))
        }
    }
}

infix fun SignalSessionDao.getByAddress(address: RemoteAddress) =
    get(address.address)

infix fun SignalSessionDao.hasSession(address: RemoteAddress) =
    hasSession(address.address)

class InvalidSessionWithAddressInsertion(details: String) :
    RuntimeException("cannot do invalid insertion with address: $details")
