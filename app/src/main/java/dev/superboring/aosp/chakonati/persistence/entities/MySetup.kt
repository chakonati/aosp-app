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
    @ColumnInfo(name = "relay_server") var relayServer: String,
    @ColumnInfo(name = "relay_server_password") var relayServerPassword: String,
    @ColumnInfo(name = "is_set_up") var isSetUp: Boolean,
) : SingleEntryEntity<MySetupDao>

