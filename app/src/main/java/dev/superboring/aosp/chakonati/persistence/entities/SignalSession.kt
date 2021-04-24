package dev.superboring.aosp.chakonati.persistence.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import org.whispersystems.libsignal.state.SessionRecord

@Entity(
    tableName = "signal_sessions",
    indices = [Index(
        value = [
            "remote_address_id",
            "device_id",
        ]
    )]
)
data class SignalSession(
    @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) var id: Int = 0,
    @ColumnInfo(name = "device_id") var deviceId: Int = 0,
    @ColumnInfo(name = "session_data") var sessionData: ByteArray,

    @ColumnInfo(name = "remote_address_id") var remoteAddressId: Int = 0,
) {

    companion object {
        infix fun from(sessionRecord: SessionRecord) =
            SignalSession(sessionData = sessionRecord.serialize())
    }

    val signalSession
        get() = SessionRecord(sessionData)

}
