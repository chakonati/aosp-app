package dev.superboring.aosp.chakonati.compose

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
inline fun <R> currentContext(content: Context.() -> R) = LocalContext.current.run(content)
