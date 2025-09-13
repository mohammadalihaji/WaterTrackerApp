package com.example.assignment_1_mad

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    private lateinit var waterCountTextView: TextView
    private lateinit var addWaterButton: Button
    private lateinit var resetButton: Button
    private lateinit var goalMessageTextView: TextView
    private lateinit var glassVolumeEditText: EditText
    private lateinit var setGlassButton: Button

    private var glassVolume = 250 // default ml per glass
    private var totalWaterGoal = 2000 // daily goal in ml
    private var currentWater = 0
    private var glassCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        progressBar = findViewById(R.id.progressBar)
        waterCountTextView = findViewById(R.id.waterCountTextView)
        addWaterButton = findViewById(R.id.addWaterButton)
        resetButton = findViewById(R.id.resetButton)
        goalMessageTextView = findViewById(R.id.goalMessageTextView)
        glassVolumeEditText = findViewById(R.id.glassVolumeEditText)
        setGlassButton = findViewById(R.id.setGlassButton)

        progressBar.max = 100
        updateUI()

        // Set glass size
        setGlassButton.setOnClickListener {
            val input = glassVolumeEditText.text.toString()
            if (input.isNotEmpty()) {
                glassVolume = input.toInt()
                resetTracker() // restart when glass size changes
                Toast.makeText(this, "Glass size set to $glassVolume ml", Toast.LENGTH_SHORT).show()
            }
        }

        // Add 1 glass
        addWaterButton.setOnClickListener {
            currentWater += glassVolume
            glassCount++
            updateUI()
        }

        // Reset
        resetButton.setOnClickListener {
            resetTracker()
        }
    }

    private fun updateUI() {
        val percentage = (currentWater * 100) / totalWaterGoal
        progressBar.progress = percentage.coerceAtMost(100)

        val totalGlassesGoal = totalWaterGoal / glassVolume
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
        updateUI()
    }
}
