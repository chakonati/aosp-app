package dev.superboring.aosp.chakonati.persistence.entities

import androidx.room.*
import androidx.room.Entity
import dev.superboring.aosp.chakonati.persistence.db
import org.whispersystems.libsignal.SignalProtocolAddress

@Entity(
    tableName = "remote_addresses",
    indices = [Index(
        value =
        [
            "address",
        ]
    )]
)
data class RemoteAddress(
    @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) val Id: Int = 0,
    @ColumnInfo(name = "address") val address: String,

    @ColumnInfo(name = "identity_key_id") var identityKeyId: Int = 0,
) {
    companion object {
        infix fun from(signalAddress: SignalProtocolAddress) =
            RemoteAddress(address = signalAddress.name)
    }

    val identityKey get() = db.remoteIdentityKeys() get identityKeyId
}
