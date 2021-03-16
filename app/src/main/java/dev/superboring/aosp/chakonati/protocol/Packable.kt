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
