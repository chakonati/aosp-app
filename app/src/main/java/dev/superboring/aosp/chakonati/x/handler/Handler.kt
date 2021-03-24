package dev.superboring.aosp.chakonati.x.handler

import android.os.Handler
import android.os.Looper

val handler get() = Handler(Looper.getMainLooper())

fun postMain(f: () -> Unit) {
    handler.post(f)
}
