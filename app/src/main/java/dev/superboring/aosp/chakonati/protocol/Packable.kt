package dev.superboring.aosp.chakonati.protocol

import org.msgpack.core.MessagePack
import org.msgpack.core.MessagePacker
import org.msgpack.core.MessageUnpacker

interface PackableBase

interface Packable<T> : PackableBase {
    fun unpack(unpacker: MessageUnpacker)
    fun pack(packer: MessagePacker)
}

fun <T> Packable<T>.packer() = MessagePack.newDefaultBufferPacker()
fun <T> Packable<T>.unpacker(bytes: ByteArray) = MessagePack.newDefaultUnpacker(bytes)

fun <T> MessagePacker.pack(packable: Packable<T>) = packable.pack(this)
fun <T> MessageUnpacker.unpack(packable: Packable<T>) = packable.unpack(this)

fun <T> MessagePacker.pack(packableList: List<Packable<T>>) {
    packArrayHeader(packableList.size)
    packableList.forEach {
        packArrayHeader(it::class.constructors.first().parameters.size)
        pack(it)
    }
}

inline fun <reified T : Packable<T>> MessageUnpacker.unpack(packableList: MutableList<Packable<T>>) {
    val size = unpackArrayHeader()
    for (i in 0 until size) {
        unpackArrayHeader()
        val pt = T::class.constructors.first().call()
        pt.unpack(this)
        packableList.add(pt)
    }
}

fun MessagePacker.packByteArray(bytes: ByteArray) {
    packBinaryHeader(bytes.size)
    writePayload(bytes)
}

fun MessageUnpacker.unpackByteArray(): ByteArray {
    val size = unpackBinaryHeader()
    return readPayload(size)
}

fun MessageUnpacker.unpackError(): Error {
    if (!tryUnpackNil()) {
        return unpackString()
    }
    return null
}

fun <T : Any?> MessageUnpacker.unpackOptional(unpack: MessageUnpacker.() -> T): T? {
    if (!tryUnpackNil()) {
        return unpack()
    }
    return null
}


fun MessageUnpacker.unpackOptionally(unpack: MessageUnpacker.() -> Unit): Unit {
    if (!tryUnpackNil()) {
        unpack()
    }
}