package dev.superboring.aosp.chakonati.persistence.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.superboring.aosp.chakonati.persistence.dao.MySetupDao

typealias IdentityPublicKey = ByteArray
typealias IdentityPrivateKey = ByteArray

@Entity(tableName = "my_setup")
data class MySetup(
    @ColumnInfo(name = "id") @PrimaryKey var id: Int = 1,
    @ColumnInfo(name = "identity_public_key") var identityPublicKey: IdentityPublicKey,
    @ColumnInfo(name = "identity_private_key") var identityPrivateKey: IdentityPrivateKey,
    @ColumnInfo(name = "registration_id") var registrationId: Int,
) : SingleEntryEntity<MySetupDao>
