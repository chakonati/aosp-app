package dev.superboring.aosp.chakonati.protocol

interface PackSerializable {
    fun serialize(): ByteArray
    fun deserialize(bytes: ByteArray)
}