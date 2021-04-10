package dev.superboring.aosp.chakonati.extras.msgpack

import android.os.Debug
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.SingletonSupport
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.paranamer.ParanamerModule
import dev.superboring.aosp.chakonati.x.debug
import kotlinx.coroutines.sync.Mutex
import kotlinx.serialization.Serializable
import org.msgpack.jackson.dataformat.MessagePackFactory
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

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
val mapperMut = ReentrantLock()

inline val @Serializable Any.serialized: ByteArray get() = mapperMut.withLock {
    mapper.writeValueAsBytes(this)
}

inline fun <reified D : @Serializable Any> ByteArray.deserialize() = mapperMut.withLock {
    try {
        mapper.readValue<D>(this)
    } catch (e: Exception) {
        debug { println("Failed to deserialize, got bytes: ${String(this)}") }
        throw DeserializationFailed(D::class.qualifiedName ?: "<no type name>", e)
    }
}

class DeserializationFailed(type: String, e: Exception) :
        RuntimeException("could not deserialize type $type", e)
