package dev.superboring.aosp.chakonati.extras.msgpack

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.serialization.Serializable

val mapper get() = jacksonObjectMapper()

inline val @Serializable Any.serialized get() = mapper.writeValueAsBytes(this)
inline fun <reified D : @Serializable Any> ByteArray.deserialize() = mapper.readValue<D>(this)
