package dev.superboring.aosp.chakonati.compose

import androidx.compose.runtime.Composable

@Composable
fun Int.stringRes() = currentContext { getString(this@stringRes) }