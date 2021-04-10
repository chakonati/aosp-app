package dev.superboring.aosp.chakonati.x

import android.os.Debug
import dev.superboring.aosp.chakonati.extensions.kotlin.then

inline fun debug(fn: () -> Unit) = Debug.isDebuggerConnected() then fn