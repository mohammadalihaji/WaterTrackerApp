package com.example.assignment_1_mad



import android.content.Context

class Prefs(context: Context) {
    private val prefs = context.getSharedPreferences("WATER_PREFS", Context.MODE_PRIVATE)

    fun saveInt(key: String, value: Int) =
        prefs.edit().putInt(key, value).apply()

    fun saveString(key: String, value: String) =
        prefs.edit().putString(key, value).apply()

    fun getInt(key: String, default: Int = 0): Int =
        prefs.getInt(key, default)

    fun getString(key: String, default: String = ""): String =
        prefs.getString(key, default) ?: default
}
