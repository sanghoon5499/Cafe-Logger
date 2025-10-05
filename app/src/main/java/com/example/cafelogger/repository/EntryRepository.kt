package com.example.cafelogger.repository

import android.content.Context
import com.example.cafelogger.model.Entry
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.IOException

class EntryRepository(private val context: Context) {
    private val fileName = "cafelogger_entries.json"
    private val json = Json { prettyPrint = true }

    fun saveEntry(entry: Entry) {
        try {
            val currentEntries = getEntries().toMutableList()
            currentEntries.add(0, entry)
            val jsonString = json.encodeToString(currentEntries)
            context.openFileOutput(fileName, Context.MODE_PRIVATE).use {
                it.write(jsonString.toByteArray())
            }
        } catch(e: IOException) {
            e.printStackTrace()
        }
    }

    fun getEntries() : List<Entry> {
        return try {
            val file = File(context.filesDir, fileName)
            if (!file.exists() || file.length() == 0L) {
                return emptyList()
            }
            val jsonString = file.readText()
            json.decodeFromString<List<Entry>>(jsonString)
        } catch(e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    fun deleteEntry(entry: Entry) {
        try {
            val currentEntries = getEntries().toMutableList()
            val wasRemoved = currentEntries.removeAll { it.timestamp == entry.timestamp }

            if (wasRemoved) {
                // Repeat file saving method from saveEntry()
                val updatedJsonString = json.encodeToString(currentEntries)
                context.openFileOutput(fileName, Context.MODE_PRIVATE).use {
                    it.write(updatedJsonString.toByteArray())
                }
            }
        } catch(e: IOException) {
            e.printStackTrace()
        }
    }
}