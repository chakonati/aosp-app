package dev.superboring.aosp.chakonati.persistence.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import dev.superboring.aosp.chakonati.domain.ChatSummary

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
    @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) val Id: Int = 0,
    @ColumnInfo(name = "remote_address_id") val remoteAddressId: Int,
    @ColumnInfo(name = "display_name") val displayName: String,
) {

    val summary get() = ChatSummary("", displayName, "")

}