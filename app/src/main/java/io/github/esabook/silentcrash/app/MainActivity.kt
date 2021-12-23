package io.github.esabook.silentcrash.app

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import io.github.esabook.silentcrash.OnCrashListener
import io.github.esabook.silentcrash.SilentCrash
import io.github.esabook.silentcrash.app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val crashListener = OnCrashListener { _, throwable ->
        AlertDialog.Builder(this)
            .setTitle("MyDialog")
            .setMessage(throwable?.stackTraceToString())
            .setPositiveButton("Dismiss") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val vBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(vBinding.root)

        vBinding.run {
            cbOnOff.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    SilentCrash
                        .init(application)
                        .autoReloadAppON(true)
                    rbRelaunch.isChecked = true

                } else {
                    SilentCrash.close()
                    rbRelaunch.isChecked = false
                    rbMyDialog.isChecked = false
                }
            }

            rbRelaunch.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked)
                    SilentCrash.autoReloadAppON(isChecked)
            }

            rbMyDialog.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked)
                    SilentCrash.getWatcher()?.setOnCrashListener(crashListener)
            }

            btException.setOnClickListener {
                throw ArithmeticException()
            }

        }
    }
}