package dev.superboring.aosp.chakonati.persistence.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

typealias PrePublicKey = ByteArray
typealias PrePrivateKey = ByteArray

@Entity(tableName = "local_pre_keys")
data class LocalPreKey(
    @ColumnInfo(name = "id") @PrimaryKey var preKeyId: Int = 0,
    @ColumnInfo(name = "public_key") var prePublicKey: PrePublicKey,
    @ColumnInfo(name = "private_key") var prePrivateKey: PrePrivateKey,
)
