package dev.superboring.aosp.chakonati.persistence.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.whispersystems.libsignal.state.SignedPreKeyRecord

typealias SignedPreKey = ByteArray

@Entity(tableName = "local_signed_pre_keys")
data class LocalSignedPreKey(
    @ColumnInfo(name = "id") @PrimaryKey var preKeyId: Int = 0,
    @ColumnInfo(name = "signed_pre_key") var signedPreKey: SignedPreKey,
) {
    val signalSignedPreKeyRecord
        get() = SignedPreKeyRecord(signedPreKey)

}
