package dev.superboring.aosp.chakonati.x.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import dev.superboring.aosp.chakonati.x.handler.postMain
import kotlin.reflect.KClass

fun Context.launchActivity(c: KClass<*>) = postMain {
    startActivity(Intent(this, c.java))
}

fun Activity.replaceActivity(c: KClass<*>) = postMain {
    startActivity(
        Intent(this, c.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK + Intent.FLAG_ACTIVITY_TASK_ON_HOME
        }
    )
    finish()
}