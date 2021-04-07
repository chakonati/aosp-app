package dev.superboring.aosp.chakonati.extras.msgpack

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.SingletonSupport
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.paranamer.ParanamerModule
import kotlinx.serialization.Serializable
import org.msgpack.jackson.dataformat.MessagePackFactory

val mapper: ObjectMapper by lazy {
    ObjectMapper(MessagePackFactory())
        .registerModule(
            KotlinModule(
                singletonSupport = SingletonSupport.CANONICALIZE,
                strictNullChecks = true,
            )
        )
        .registerModule(ParanamerModule())
}

inline val @Serializable Any.serialized: ByteArray get() = mapper.writeValueAsBytes(this)
inline fun <reified D : @Serializable Any> ByteArray.deserialize() = mapper.readValue<D>(this)
