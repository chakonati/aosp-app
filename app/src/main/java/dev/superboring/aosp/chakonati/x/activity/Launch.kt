package dev.superboring.aosp.chakonati.x.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import dev.superboring.aosp.chakonati.extras.msgpack.deserialize
import dev.superboring.aosp.chakonati.extras.msgpack.serialized
import dev.superboring.aosp.chakonati.x.handler.postMain
import kotlinx.serialization.Serializable
import kotlin.reflect.KClass

const val serializedParametersKey = "serializedParameters"

fun Intent.replacing() = apply {
    flags = Intent.FLAG_ACTIVITY_NEW_TASK + Intent.FLAG_ACTIVITY_TASK_ON_HOME
}

fun Context.launchActivity(c: KClass<*>) = postMain {
    startActivity(Intent(this, c.java))
}

fun Activity.replaceActivity(c: KClass<*>) = postMain {
    startActivity(
        Intent(this, c.java).replacing()
    )
    finish()
}

inline fun <reified B : @Serializable Any> Intent.putParameters(parameters: B) =
    putExtra(serializedParametersKey, parameters.serialized)

inline fun <reified B : @Serializable Any> Context.launchActivity(c: KClass<*>, parameters: B) {
    startActivity(
        Intent(this, c.java).putParameters(parameters)
    )
}

inline fun <reified B : @Serializable Any> Activity.replaceActivity(c: KClass<*>, parameters: B) {
    startActivity(
        Intent(this, c.java).putParameters(parameters).replacing()
    )
    finish()
}

inline fun <reified D : @Serializable Any> Activity.parameters(): D? {
    return intent.extras?.getByteArray(serializedParametersKey)?.deserialize()
}
