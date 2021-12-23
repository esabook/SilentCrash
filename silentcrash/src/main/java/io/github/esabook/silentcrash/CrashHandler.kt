package io.github.esabook.silentcrash

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.util.Log
import java.lang.ref.WeakReference

class CrashHandler internal constructor(app: Application) : Thread.UncaughtExceptionHandler {
    val TAG = this.javaClass.simpleName
    private val weakApp = WeakReference(app)
    private val mainHandler = Handler(Looper.getMainLooper())

    private var defaultExceptionHandler: Thread.UncaughtExceptionHandler? =
        Thread.getDefaultUncaughtExceptionHandler()

    /**
     * collection of SilentCrashListener, invoked when crash occurred
     */
    private var onCrashListener = WeakReference<OnCrashListener>(null)

    override fun uncaughtException(t: Thread, e: Throwable) {
        invokeCrashListener(e)
    }

    private fun attach() {
        mainHandler.post(this::looperWatch)
        Thread.setDefaultUncaughtExceptionHandler(this@CrashHandler)
    }

    internal fun detach() {
        setOnCrashListener(null)
        mainHandler.removeCallbacks(this::looperWatch)
        Thread.setDefaultUncaughtExceptionHandler(defaultExceptionHandler)
        defaultExceptionHandler = null
        Log.i(TAG, "detach()")
    }

    fun setOnCrashListener(listener: OnCrashListener?) {
        onCrashListener = WeakReference(listener)
    }


    private fun looperWatch() {
        while (defaultExceptionHandler != null) {
            try {
                Looper.loop()
            } catch (ex: Exception) {
                if (defaultExceptionHandler == null)
                    throw findCause(ex)

                invokeCrashListener(ex)
            }
        }
    }

    private fun findCause(exception: Throwable): Throwable {
        var prev: Throwable? = null
        var cause: Throwable = exception
        while (cause.cause != null && cause != prev) {
            prev = cause
            cause = cause.cause ?: prev
        }
        cause.stackTrace.filterNot { it.className.contains(TAG) }
            .let {
                cause.stackTrace = it.toTypedArray()
            }

        return cause
    }

    private fun invokeCrashListener(ex: Throwable) {
        val thrwble = findCause(ex)
        onCrashListener.get()?.onCrash(weakApp.get(), thrwble)
    }

    init {
        attach()
    }
}