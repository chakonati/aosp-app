package dev.superboring.aosp.chakonati.x.clipboard

import android.content.ClipboardManager
import android.content.Context

val Context.clipboard get() = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
