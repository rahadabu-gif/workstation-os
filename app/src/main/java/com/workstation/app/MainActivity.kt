package com.workstation.app

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat

class MainActivity : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences

    private val motivationalQuotes = arrayOf(
        "“Discipline is choosing between what you want now and what you want most.”",
        "“Don’t count the days, make the days count.”",
        "“Action creates clarity. Start the first 10 minutes now.”",
        "“Small daily efforts compound into unstoppable mastery.”"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prefs = getSharedPreferences("WorkstationPrefs", Context.MODE_PRIVATE)

        val tvMotivation = findViewById<TextView>(R.id.tvMotivationBanner)
        val tvCompletionRate = findViewById<TextView>(R.id.tvCompletionRate)
        val tvDistractions = findViewById<TextView>(R.id.tvDistractions)
        val pbCompletion = findViewById<ProgressBar>(R.id.pbCompletion)

        val etTargetPlan = findViewById<EditText>(R.id.etTargetPlan)
        val cbTask1 = findViewById<CheckBox>(R.id.cbTask1)
        val cbTask2 = findViewById<CheckBox>(R.id.cbTask2)
        val cbTask3 = findViewById<CheckBox>(R.id.cbTask3)
        val cbTask4 = findViewById<CheckBox>(R.id.cbTask4)

        val switchEnforce = findViewById<SwitchCompat>(R.id.switchEnforce)
        val switchVoice = findViewById<SwitchCompat>(R.id.switchVoice)
        val etCustomVoice = findViewById<EditText>(R.id.etCustomVoice)
        val btnSaveSettings = findViewById<Button>(R.id.btnSaveSettings)
        val btnOpenAccessibility = findViewById<Button>(R.id.btnOpenAccessibility)

        tvMotivation.text = motivationalQuotes.random()
        etTargetPlan.setText(prefs.getString("daily_target_plan", ""))
        cbTask1.isChecked = prefs.getBoolean("task_1", false)
        cbTask2.isChecked = prefs.getBoolean("task_2", false)
        cbTask3.isChecked = prefs.getBoolean("task_3", false)
        cbTask4.isChecked = prefs.getBoolean("task_4", false)

        switchEnforce.isChecked = prefs.getBoolean("enforcement_enabled", true)
        switchVoice.isChecked = prefs.getBoolean("voice_enabled", true)
        etCustomVoice.setText(prefs.getString("custom_voice_text", "Stop scrolling! Put the phone down and work."))

        fun updateAnalytics() {
            var completedCount = 0
            if (cbTask1.isChecked) completedCount++
            if (cbTask2.isChecked) completedCount++
            if (cbTask3.isChecked) completedCount++
            if (cbTask4.isChecked) completedCount++

            val percentage = (completedCount * 100) / 4
            pbCompletion.progress = percentage
            tvCompletionRate.text = "Activities Completed: $completedCount / 4 ($percentage%)"

            val blockedCount = prefs.getInt("distraction_count", 0)
            tvDistractions.text = "Distractions Blocked Today: $blockedCount"
        }

        updateAnalytics()

        val taskCheckListener = CompoundButton.OnCheckedChangeListener { _, _ ->
            prefs.edit()
                .putBoolean("task_1", cbTask1.isChecked)
                .putBoolean("task_2", cbTask2.isChecked)
                .putBoolean("task_3", cbTask3.isChecked)
                .putBoolean("task_4", cbTask4.isChecked)
                .apply()
            updateAnalytics()
        }

        cbTask1.setOnCheckedChangeListener(taskCheckListener)
        cbTask2.setOnCheckedChangeListener(taskCheckListener)
        cbTask3.setOnCheckedChangeListener(taskCheckListener)
        cbTask4.setOnCheckedChangeListener(taskCheckListener)

        btnSaveSettings.setOnClickListener {
            prefs.edit()
                .putString("daily_target_plan", etTargetPlan.text.toString())
                .putBoolean("enforcement_enabled", switchEnforce.isChecked)
                .putBoolean("voice_enabled", switchVoice.isChecked)
                .putString("custom_voice_text", etCustomVoice.text.toString())
                .apply()
            Toast.makeText(this, "Plan and Settings saved successfully!", Toast.LENGTH_SHORT).show()
        }

        btnOpenAccessibility.setOnClickListener {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            startActivity(intent)
            Toast.makeText(this, "Turn ON 'Workstation OS' to enable app blocking", Toast.LENGTH_LONG).show()
        }
    }

    override fun onResume() {
        super.onResume()
        val tvDistractions = findViewById<TextView>(R.id.tvDistractions)
        val blockedCount = prefs.getInt("distraction_count", 0)
        tvDistractions.text = "Distractions Blocked Today: $blockedCount"
    }
}
