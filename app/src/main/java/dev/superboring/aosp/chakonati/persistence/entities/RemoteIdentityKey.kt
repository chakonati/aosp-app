package dev.superboring.aosp.chakonati.persistence.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.superboring.aosp.chakonati.x.logging.logDebug
import org.whispersystems.libsignal.IdentityKey

@Entity(tableName = "remote_identity_keys")
data class RemoteIdentityKey(
    @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) var id: Int = 0,
    @ColumnInfo(name = "public_key") var publicKey: IdentityPublicKey,
) {

    val signalIdentityKey get() = IdentityKey(publicKey).apply {
        this@RemoteIdentityKey.logDebug(
            "Constructing Signal identity key out of remote identity key $id"
        )
    }

}
