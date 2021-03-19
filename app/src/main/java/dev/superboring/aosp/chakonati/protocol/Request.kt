package dev.superboring.aosp.chakonati.protocol

import org.msgpack.core.MessagePacker
import org.msgpack.core.MessageUnpacker

typealias RequestId = Long
private var nextId: RequestId = 0L

abstract class Request<R : Response>(
    var action: String,
    var argLen: Int = 0,
    var id: RequestId = nextId++,
) : Packable<Request<R>>, PackSerializable {
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

    abstract fun newResponse(): R

}
