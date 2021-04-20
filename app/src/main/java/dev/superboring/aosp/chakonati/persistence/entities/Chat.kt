package dev.superboring.aosp.chakonati.persistence.entities

import androidx.room.*
import androidx.room.Entity
import dev.superboring.aosp.chakonati.domain.ChatSummary
import dev.superboring.aosp.chakonati.persistence.db

@Entity(
    tableName = "chats",
    indices = [Index(
        value =
        [
            "remote_address_id"
        ]
    )]
)
data class Chat(
    @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "remote_address_id") val remoteAddressId: Int,
    @ColumnInfo(name = "display_name") val displayName: String,
) {

    suspend fun summary() = db.withTransaction {
        ChatSummary(
            id,
            db.remoteAddresses().getAddress(remoteAddressId).address,
            displayName,
            // TODO: fetch last message
            "",
        )
    }

}
