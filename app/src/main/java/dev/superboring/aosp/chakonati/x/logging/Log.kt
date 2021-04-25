package dev.superboring.aosp.chakonati.x.logging

import android.util.Log
import dev.superboring.aosp.chakonati.extensions.t.tag
import dev.superboring.aosp.chakonati.x.debug

inline fun <reified T> T.log(message: String, level: Int = Log.DEBUG) =
    Log.println(level, tag(), message)

inline fun <reified T> T.logDebug(message: String) = debug {
    log(message, level = Log.DEBUG)
}

inline fun <reified T> T.logDebug(format: String = "", vararg args: Any?, run: T.() -> Unit = {}) {
    debug {
        logDebug(String.format(format, *args))
    }
    run()
}

inline fun <reified T> T.log(level: Int = Log.DEBUG, format: String = "", vararg args: Any?) =
    log(String.format(format, *args), level)