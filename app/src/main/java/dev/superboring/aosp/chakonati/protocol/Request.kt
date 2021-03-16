package dev.superboring.aosp.chakonati.protocol

import org.msgpack.core.MessagePacker
import org.msgpack.core.MessageUnpacker

private var nextId: Long = 0L

abstract class Request(
    var action: String,
    var argLen: Int = 0,
    private var id: Long = nextId++,
) : Packable<Request>, PackSerializable {
    override fun serialize(): ByteArray {
        return packer().apply {
            packMapHeader(3)
            packString(::id.name)
            packLong(id)
            packString(::action.name)
            packString(action)
            packString("data")
            packArrayHeader(argLen)
            pack(this)
        }.toByteArray()
    }

    override fun deserialize(bytes: ByteArray) {
        unpacker(bytes).run {
            unpackMapHeader()
            unpackString()
            id = unpackLong()
            unpackString()
            action = unpackString()
            unpackString()
            argLen = unpackArrayHeader()
            unpack(this)
        }
    }

    override fun pack(packer: MessagePacker) {

    }

    override fun unpack(unpacker: MessageUnpacker) {

    }

}
