package com.example.assignment_1_mad

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    private lateinit var waterCountTextView: TextView
    private lateinit var addWaterButton: Button
    private lateinit var resetButton: Button
    private lateinit var goalMessageTextView: TextView
    private lateinit var glassVolumeEditText: EditText
    private lateinit var setGlassButton: Button
    private lateinit var historyButton: Button

    private lateinit var prefs: Prefs
    private var glassVolume = 250 // default ml per glass
    private var totalWaterGoal = 2000 // daily goal in ml
    private var currentWater = 0
    private var glassCount = 0

    // history stored as "date: X ml, Y glasses" separated by "|"
    private var historyList: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prefs = Prefs(this)

        progressBar = findViewById(R.id.progressBar)
        waterCountTextView = findViewById(R.id.waterCountTextView)
        addWaterButton = findViewById(R.id.addWaterButton)
        resetButton = findViewById(R.id.resetButton)
        goalMessageTextView = findViewById(R.id.goalMessageTextView)
        glassVolumeEditText = findViewById(R.id.glassVolumeEditText)
        setGlassButton = findViewById(R.id.setGlassButton)
        historyButton = findViewById(R.id.historyButton)

        progressBar.max = 100

        // load preferences
        loadSavedProgress()
        loadHistory()

        // check if day changed (auto-reset)
        checkDailyReset()

        updateUI()

        setGlassButton.setOnClickListener {
            val input = glassVolumeEditText.text.toString()
            if (input.isNotEmpty()) {
                glassVolume = input.toInt()
                prefs.saveInt("glassVolume", glassVolume)
                resetTracker()
                Toast.makeText(this, "Glass size set to $glassVolume ml", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Enter glass ml", Toast.LENGTH_SHORT).show()
            }
        }

        addWaterButton.setOnClickListener {
            currentWater += glassVolume
            glassCount++
            saveProgress()
            updateUI()
            if (currentWater >= totalWaterGoal) {
                goalMessageTextView.visibility = TextView.VISIBLE
            }
        }

        resetButton.setOnClickListener {
            // save today's record into history before manual reset
            saveTodayToHistory()
            resetTracker()
            Toast.makeText(this, "Tracker reset", Toast.LENGTH_SHORT).show()
        }

        historyButton.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            intent.putStringArrayListExtra("list", ArrayList(historyList))
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        // If app was closed overnight, check date again
        checkDailyReset()
        loadSavedProgress()
        updateUI()
    }

    private fun loadSavedProgress() {
        glassVolume = prefs.getInt("glassVolume", 250)
        currentWater = prefs.getInt("currentWater", 0)
        glassCount = prefs.getInt("glassCount", 0)
    }

    private fun loadHistory() {
        val raw = prefs.getString("history", "")
        historyList = if (raw.isEmpty()) mutableListOf() else raw.split("|").toMutableList()
    }

    private fun saveProgress() {
        prefs.saveInt("currentWater", currentWater)
        prefs.saveInt("glassCount", glassCount)
        prefs.saveString("lastDate", todayDate())
    }

    private fun updateUI() {
        val percentage = if (totalWaterGoal == 0) 0 else (currentWater * 100) / totalWaterGoal
        progressBar.progress = percentage.coerceAtMost(100)

        val totalGlassesGoal = if (glassVolume == 0) 0 else (totalWaterGoal / glassVolume)
        waterCountTextView.text = "$glassCount / $totalGlassesGoal glasses\n($currentWater ml)"

        if (currentWater >= totalWaterGoal) {
            goalMessageTextView.visibility = TextView.VISIBLE
        } else {
            goalMessageTextView.visibility = TextView.GONE
        }
    }

    private fun resetTracker() {
        currentWater = 0
        glassCount = 0
        saveProgress()
        updateUI()
    }

    private fun todayDate(): String {
        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun checkDailyReset() {
        val lastDate = prefs.getString("lastDate", "")
        val today = todayDate()
        if (lastDate.isNotEmpty() && lastDate != today) {
            // day changed — save yesterday's summary then reset
            saveTodayToHistory(date = lastDate, ml = prefs.getInt("currentWater", 0), glasses = prefs.getInt("glassCount", 0))
            resetTracker()
        } else if (lastDate.isEmpty()) {
            // initialize lastDate
            prefs.saveString("lastDate", today)
        }
    }

    // save the provided day record (or today's record if date param omitted)
    private fun saveTodayToHistory(date: String = todayDate(), ml: Int = currentWater, glasses: Int = glassCount) {
        val record = "$date: $ml ml, $glasses glasses"
        // avoid duplicate same-date entries — replace if exists
        val existingIndex = historyList.indexOfFirst { it.startsWith("$date:") }
        if (existingIndex >= 0) historyList[existingIndex] = record else historyList.add(record)
        prefs.saveString("history", historyList.joinToString("|"))
    }
}
