package com.example.assignment_1_mad



import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class HistoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        val historyList = intent.getStringArrayListExtra("list") ?: arrayListOf()
        val listView = findViewById<ListView>(R.id.historyListView)

        // show most recent first
        val reversed = ArrayList(historyList.reversed())
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, reversed)
        listView.adapter = adapter
    }
}
