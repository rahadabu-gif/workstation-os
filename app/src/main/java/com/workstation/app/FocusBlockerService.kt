package com.workstation.app

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.SharedPreferences
import android.speech.tts.TextToSpeech
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast
import java.util.Locale

class FocusBlockerService : AccessibilityService(), TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isTtsReady = false
    private lateinit var prefs: SharedPreferences

    private val blockedPackages = setOf(
        "com.facebook.katana",
        "com.facebook.orca",
        "com.instagram.android",
        "com.zhiliaoapp.musically",
        "com.google.android.youtube"
    )

    override fun onServiceConnected() {
        super.onServiceConnected()
        prefs = getSharedPreferences("WorkstationPrefs", Context.MODE_PRIVATE)
        tts = TextToSpeech(this, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = Locale.US
            isTtsReady = true
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            val packageName = event.packageName?.toString() ?: return

            val isEnforcing = prefs.getBoolean("enforcement_enabled", true)
            if (!isEnforcing) return

            if (blockedPackages.contains(packageName)) {
                performGlobalAction(GLOBAL_ACTION_HOME)

                val currentDistractions = prefs.getInt("distraction_count", 0)
                prefs.edit().putInt("distraction_count", currentDistractions + 1).apply()

                val customText = prefs.getString("custom_warning_text", "Focus on your goals. Facebook is blocked.")
                Toast.makeText(applicationContext, customText, Toast.LENGTH_LONG).show()

                val voiceEnabled = prefs.getBoolean("voice_enabled", true)
                if (voiceEnabled && isTtsReady) {
                    val customVoice = prefs.getString("custom_voice_text", "Warning! Put the phone down and get back to your workstation.")
                    tts?.speak(customVoice, TextToSpeech.QUEUE_FLUSH, null, "FocusWarning")
                }
            }
        }
    }

    override fun onInterrupt() {}

    override fun onDestroy() {
        tts?.stop()
        tts?.shutdown()
        super.onDestroy()
    }
}
