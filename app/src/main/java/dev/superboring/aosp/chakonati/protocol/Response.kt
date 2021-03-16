package dev.superboring.aosp.chakonati.protocol

import org.msgpack.core.MessagePacker
import org.msgpack.core.MessageUnpacker

abstract class Response(
    var argLen: Int = 0,
    private var id: Long = 0L,
) : Packable<Response>, PackSerializable {
    override fun serialize(): ByteArray {
        return packer().apply {
            packMapHeader(3)
            packString(::id.name)
            packLong(id)
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
            argLen = unpackArrayHeader()
            unpack(this)
        }
    }

    override fun pack(packer: MessagePacker) {

    }

    override fun unpack(unpacker: MessageUnpacker) {

    }

}
