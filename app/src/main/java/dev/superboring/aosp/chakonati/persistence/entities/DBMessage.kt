package dev.superboring.aosp.chakonati.persistence.entities

import androidx.room.*
import androidx.room.Entity
import dev.superboring.aosp.chakonati.components.fragments.chat.Message
import dev.superboring.aosp.chakonati.components.fragments.chat.MessageFrom

@Entity(
    tableName = "messages",
    indices = [Index(
        value =
        [
            "chat_id"
        ]
    )]
)
data class DBMessage(
    @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "chat_id") val chatId: Int = 0,
    @ColumnInfo(name = "from") val messageFrom: Int,
    @ColumnInfo(name = "sender") val sender: String? = null,
    @ColumnInfo(name = "message_text") val messageText: String,
    @ColumnInfo(name = "message_data") val messageData: ByteArray = byteArrayOf(),
) {

    val asBubbleMessage get() = Message(
        MessageFrom.fromInt(messageFrom),
        messageText,
    )

}
