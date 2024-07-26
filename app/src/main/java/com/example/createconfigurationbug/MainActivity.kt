package com.example.createconfigurationbug

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Locale

class MainActivity : AppCompatActivity() {

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocaleManager.wrapContext(base))
    }

    private var setMode: TextView? = null
    private var localMode: TextView? = null
    private var defaultMode: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)


        findViewById<TextView>(R.id.app_name_activity_context).apply {
            text = "Context resources: ${getString(R.string.app_name)}"
        }
        findViewById<TextView>(R.id.app_name_app_context).apply {
            text = "App resources: ${application.applicationContext.getString(R.string.app_name)}"
        }

        localMode = findViewById<TextView>(R.id.default_mode_text)
        defaultMode = findViewById<TextView>(R.id.local_mode_text)
        setMode = findViewById<TextView>(R.id.set_mode_text)

        updateLabels(
            null,
            delegate.localNightMode,
            AppCompatDelegate.getDefaultNightMode()
        )

        findViewById<Button>(R.id.button_light).setOnClickListener {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            updateLabels(
                AppCompatDelegate.MODE_NIGHT_NO,
                delegate.localNightMode,
                AppCompatDelegate.getDefaultNightMode()
            )
        }

        findViewById<Button>(R.id.button_dark).setOnClickListener {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            updateLabels(
                AppCompatDelegate.MODE_NIGHT_YES,
                delegate.localNightMode,
                AppCompatDelegate.getDefaultNightMode()
            )
        }

        findViewById<Button>(R.id.button_system).setOnClickListener {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            updateLabels(
                AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM,
                delegate.localNightMode,
                AppCompatDelegate.getDefaultNightMode()
            )
        }

        findViewById<Button>(R.id.button_recreate).setOnClickListener {
            recreate()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun updateLabels(mode: Int?, modeLocal: Int, modeDefault: Int) {
        setMode?.text = "Mode set to: ${mapNightMode(mode)}"
        localMode?.text = "Local mode set to: ${mapNightMode(modeLocal)}"
        defaultMode?.text = "Default mode set to: ${mapNightMode(modeDefault)}"
    }

    private fun mapNightMode(nightMode: Int?): String = when (nightMode) {
        AppCompatDelegate.MODE_NIGHT_NO -> "Light(NightMode.MODE_NIGHT_NO)"
        AppCompatDelegate.MODE_NIGHT_YES -> "Dark(NightMode.MODE_NIGHT_YES)"
        AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> "System(NightMode.MODE_NIGHT_FOLLOW_SYSTEM)"
        AppCompatDelegate.MODE_NIGHT_AUTO_TIME -> "deprecated Auto(NightMode.MODE_NIGHT_AUTO_TIME)"
        AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY -> "Battery(NightMode.MODE_NIGHT_AUTO_BATTERY)"
        AppCompatDelegate.MODE_NIGHT_UNSPECIFIED -> "Unspecified(NightMode.MODE_NIGHT_UNSPECIFIED)"
        null -> "Not set"
        else -> "None"
    }
}

object LocaleManager {

    fun wrapContext(context: Context): Context {
        val preferredLocale = Locale("cs", "CZ")
        Locale.setDefault(preferredLocale)

        val config = context.resources.configuration.apply {
            setLocale(preferredLocale)
        }
        return context.createConfigurationContext(config)
    }

    fun wrapAppContext(context: Context): Context {
        return when(0) {
            0 -> wrapContext(context) // Bugged
            1 -> wrapAppContextDeprecated(context) // Deprecated
            else -> context // No change
        }
    }

    private fun wrapAppContextDeprecated(context: Context): Context {
        val preferredLocale = Locale("cs", "CZ")
        Locale.setDefault(preferredLocale)

        context.resources.apply {
            configuration.setLocale(preferredLocale)
            updateConfiguration(configuration, displayMetrics)
        }
        return context
    }
}
