package dev.superboring.aosp.chakonati.persistence.entities

import androidx.room.*
import androidx.room.Entity
import dev.superboring.aosp.chakonati.domain.ChatSummary
import dev.superboring.aosp.chakonati.domain.LastMessage
import dev.superboring.aosp.chakonati.persistence.db
import dev.superboring.aosp.chakonati.signal.ChatSessionDetails

@Entity(
    tableName = "chats",
    indices = [Index(
        value =
        [
            "remote_address_id",
            "device_id",
        ]
    )]
)
data class Chat(
    @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) var id: Long = 0,
    @ColumnInfo(name = "remote_address_id") val remoteAddressId: Int,
    @ColumnInfo(name = "device_id") val deviceId: Int,
    @ColumnInfo(name = "display_name") val displayName: String,
) {

    suspend fun summary() = db.withTransaction {
        ChatSummary(
            id,
            db.remoteAddresses().getAddress(remoteAddressId).address,
            displayName,
            db.messages().last(id, 1).value?.lastOrNull()?.messageText ?: "",
        )
    }

    val remoteAddress get() = db.remoteAddresses().getAddress(remoteAddressId)

    val sessionDetails get() = ChatSessionDetails(remoteAddress.address, deviceId)

}
