package dev.superboring.aosp.chakonati.x

import android.os.Debug
import dev.superboring.aosp.chakonati.BuildConfig
import dev.superboring.aosp.chakonati.extensions.kotlin.then

inline fun debug(fn: () -> Unit) = BuildConfig.DEBUG then fn
inline fun ifDebugger(fn: () -> Unit) = Debug.isDebuggerConnected() then fn
