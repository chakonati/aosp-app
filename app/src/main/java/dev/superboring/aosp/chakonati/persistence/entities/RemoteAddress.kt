package dev.superboring.aosp.chakonati.persistence.entities

import androidx.room.*
import androidx.room.Entity
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

    @ColumnInfo(name = "identity_key_id") val identityKeyId: Int = 0,
) {
    companion object {
        infix fun from(signalAddress: SignalProtocolAddress) =
            RemoteAddress(address = signalAddress.name)
    }
}

data class RemoteAddressAndIdentityKey(
    @Embedded val address: RemoteAddress,
    @Relation(
        parentColumn = "identity_key_id",
        entityColumn = "id"
    )
    val identityKey: RemoteIdentityKey?
)
