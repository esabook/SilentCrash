package io.github.esabook.silentcrash

import android.app.Application
import android.content.Intent
import android.util.Log
import java.io.Closeable

object SilentCrash : Closeable {
    private val TAG: String = this.javaClass.simpleName
    const val RELOAD_INTENT_FROM_SILENT_CRASH = "intent_from_silent_crash"

    private var crashHandler: CrashHandler? = null

    /**
     * app will be relaunch when occurred
     */
    private var isAutoReloadON = true

    private val reloadApplicationActioner = OnCrashListener { a, _ ->
        if (a == null) return@OnCrashListener
        Log.i(TAG, "Initiate to relaunching app from context: ${a.javaClass.simpleName}")

        val defaultIntent = a.packageManager.getLaunchIntentForPackage(a.packageName)?.apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            putExtra(RELOAD_INTENT_FROM_SILENT_CRASH, true)
        }
        a.startActivity(defaultIntent)
        Runtime.getRuntime().exit(0)
        Log.v(TAG, "Initiate to relaunching app to Intent: ${defaultIntent?.component?.className}")
    }

    fun init(app: Application): SilentCrash {
        if (crashHandler != null) {
            Log.v(TAG, "Init skipped, call SilentCrash.close() for re-init")
            return this
        }
        crashHandler = CrashHandler(app)
        autoReloadAppON(isAutoReloadON)
        return this
    }

    fun autoReloadAppON(on: Boolean): SilentCrash {
        isAutoReloadON = on
        if (isAutoReloadON) {
            crashHandler?.setOnCrashListener(reloadApplicationActioner)
        } else {
            crashHandler?.setOnCrashListener(null)
        }
        return this
    }

    fun getWatcher() = crashHandler


    override fun close() {
        crashHandler?.detach()
        crashHandler = null
        Log.v(TAG, "Closed")
    }
}