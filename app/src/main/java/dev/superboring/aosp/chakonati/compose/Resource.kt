package dev.superboring.aosp.chakonati.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun Int.stringRes() = currentContext { getString(this@stringRes) }