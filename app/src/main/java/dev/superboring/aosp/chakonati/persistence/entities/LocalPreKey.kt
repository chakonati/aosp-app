package dev.superboring.aosp.chakonati.persistence.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.whispersystems.libsignal.ecc.Curve
import org.whispersystems.libsignal.ecc.ECKeyPair
import org.whispersystems.libsignal.state.PreKeyRecord

typealias PrePublicKey = ByteArray
typealias PrePrivateKey = ByteArray

@Entity(tableName = "local_pre_keys")
data class LocalPreKey(
    @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) var preKeyId: Int = 0,
    @ColumnInfo(name = "public_key") var prePublicKey: PrePublicKey,
    @ColumnInfo(name = "private_key") var prePrivateKey: PrePrivateKey,
) {
    val signalPreKeyRecord
        get() =
            PreKeyRecord(
                preKeyId, ECKeyPair(
                    Curve.decodePoint(prePublicKey, 0),
                    Curve.decodePrivatePoint(prePrivateKey)
                )
            )
}
