package com.choi.cafelogger.model

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.json.JSONArray
import org.json.JSONObject

class HomeViewModel(app: Application) : AndroidViewModel(app) {

    private val prefs = app.getSharedPreferences("cafelogger_prefs", Context.MODE_PRIVATE)
    private val key = "uploads_json"

    private val _allUploads = MutableLiveData<List<UploadItem>>(emptyList())
    val allUploads: LiveData<List<UploadItem>> = _allUploads

    init { refresh() }

    fun refresh() {
        _allUploads.value = readFromPrefs().sortedByDescending { it.timestamp }
    }

    fun getAllUploadsJsonArray(): JSONArray {
        val existing = prefs.getString(key, "[]")
        return try { JSONArray(existing) } catch (_: Exception) { JSONArray() }
    }

    private fun readFromPrefs(): List<UploadItem> {
        val arr = getAllUploadsJsonArray()
        val out = ArrayList<UploadItem>(arr.length())
        for (i in 0 until arr.length()) {
            val o: JSONObject = arr.optJSONObject(i) ?: continue
            out += UploadItem(
                timestamp   = o.optLong("timestamp"),
                location    = o.optString("location", null),
                type        = o.optString("type", null),
                roast       = o.optString("roast", null),
                origin      = o.optString("origin", null),
                process     = o.optString("process", null),
                drinkStyle  = o.optString("drinkStyle", null),
                imageUri    = o.optString("imageUri", null)
            )
        }
        return out
    }

    fun addUpload(item: UploadItem) {
        val arr = getAllUploadsJsonArray()
        arr.put(JSONObject().apply {
            put("timestamp", item.timestamp)
            put("location", item.location)
            put("type", item.type)
            put("roast", item.roast)
            put("origin", item.origin)
            put("process", item.process)
            put("drinkStyle", item.drinkStyle)
            put("imageUri", item.imageUri)
        })
        prefs.edit().putString(key, arr.toString()).apply()
        refresh()
    }

    fun clearAll() {
        prefs.edit().putString(key, "[]").apply()
        refresh()
    }
}
