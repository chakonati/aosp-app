package dev.superboring.aosp.chakonati.persistence.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.superboring.aosp.chakonati.x.logging.logDebug
import org.whispersystems.libsignal.ecc.Curve
import org.whispersystems.libsignal.ecc.ECKeyPair
import org.whispersystems.libsignal.state.PreKeyRecord

typealias PrePublicKey = ByteArray
typealias PrePrivateKey = ByteArray

@Entity(tableName = "local_pre_keys")
data class LocalPreKey(
    @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) var id: Int = 0,
    @ColumnInfo(name = "key_id") var preKeyId: Int,
    @ColumnInfo(name = "public_key") var prePublicKey: PrePublicKey,
    @ColumnInfo(name = "private_key") var prePrivateKey: PrePrivateKey,
) {
    val signalPreKeyRecord: PreKeyRecord
        get() {
            logDebug("Converting LocalPreKey $this to signal pre key record")
            return PreKeyRecord(
                preKeyId, ECKeyPair(
                    Curve.decodePoint(prePublicKey, 0),
                    Curve.decodePrivatePoint(prePrivateKey)
                )
            )
        }
}
