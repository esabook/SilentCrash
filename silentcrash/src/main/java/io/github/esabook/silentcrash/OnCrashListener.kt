package io.github.esabook.silentcrash

import android.app.Activity
import android.app.Application

fun interface OnCrashListener {
    fun onCrash(app: Application?, e: Throwable?)
}