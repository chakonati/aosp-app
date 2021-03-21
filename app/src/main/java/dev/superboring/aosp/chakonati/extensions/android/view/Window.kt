package dev.superboring.aosp.chakonati.extensions.android.view

import android.graphics.Color
import android.view.Window

fun Window.useTranslucentBars() {
    statusBarColor = Color.TRANSPARENT
}