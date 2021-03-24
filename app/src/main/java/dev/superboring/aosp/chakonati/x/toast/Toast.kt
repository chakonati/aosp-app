package dev.superboring.aosp.chakonati.x.toast

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import dev.superboring.aosp.chakonati.x.handler.postMain

fun Context.showToast(@StringRes res: Int, duration: Int = Toast.LENGTH_SHORT) = postMain {
    Toast.makeText(this, res, duration).show()
}