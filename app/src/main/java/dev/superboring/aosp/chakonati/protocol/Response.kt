package dev.superboring.aosp.chakonati.protocol

import org.msgpack.core.MessagePacker
import org.msgpack.core.MessageUnpacker

const val headerSize = 2
const val dataKey = "data"

abstract class Response(
    var argLen: Int = 0,
    var id: RequestId = 0L,
) : Packable<Response>, PackSerializable {
    override fun serialize(): ByteArray {
        return packer().apply {
            packMapHeader(headerSize + argLen)
            packString(::id.name)
            packLong(id)
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

    override fun pack(packer: MessagePacker) {

    }

    override fun unpack(unpacker: MessageUnpacker) {

    }

}
