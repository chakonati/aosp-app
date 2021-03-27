package dev.superboring.aosp.chakonati.protocol

import org.msgpack.core.MessagePacker
import org.msgpack.core.MessageUnpacker


typealias Error = String?

const val headerSize = 3
const val dataKey = "data"

enum class MessageType {
    REQUEST,
    RESPONSE,
    ONEWAY,
    STREAM,

    UNKNOWN
}

abstract class Message<T>(
    var argLen: Int = 0,
    var id: RequestId = 0L,
    var messageType: MessageType = MessageType.UNKNOWN
) : Packable<T>, PackSerializable {
    override fun serialize(): ByteArray {
        return packer().apply {
            packMapHeader(headerSize + argLen)
            packString(::id.name)
            packLong(id)
            packString(::messageType.name)
            packInt(messageType.ordinal)
            packString(dataKey)
            packArrayHeader(argLen)
            pack(this)
        }.toByteArray()
    }

    override fun deserialize(bytes: ByteArray) {
        unpacker(bytes).run {
            unpackMapHeader()
            for (i in 0 until headerSize) {
                try {
                    when (unpackString()) {
                        ::id.name -> id = unpackLong()
                        ::messageType.name -> messageType = MessageType.values()[unpackInt()]
                        dataKey -> {
                            argLen = unpackArrayHeader()
                            unpack(this)
                        }
                    }
                } catch (e: Exception) {
                    throw RuntimeException("Exception during unpack at index $i", e)
                }
            }

        }
    }

    abstract override fun pack(packer: MessagePacker)

    abstract override fun unpack(unpacker: MessageUnpacker)

}

open class MessageHeader : Message<MessageHeader>() {
    override fun unpack(unpacker: MessageUnpacker) {

    }

    override fun pack(packer: MessagePacker) {

    }
}
