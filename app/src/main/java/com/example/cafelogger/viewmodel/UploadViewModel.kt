package com.example.cafelogger.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cafelogger.model.Entry
import com.example.cafelogger.repository.EntryRepository
import kotlinx.coroutines.launch

class UploadViewModel(private val entryRepository: EntryRepository) : ViewModel() {
    fun saveNewEntry(
        location: String,
        type: String,
        roastLevel: String,
        origin: String,
        process: String,
        drinkStyle: String,
        imageUri: String?,
        timestamp: Long)
    {
        viewModelScope.launch {
            val newEntry = Entry(
                location = location,
                type = type,
                roastLevel = roastLevel,
                origin = origin,
                process = process,
                drinkStyle = drinkStyle,
                imageUri = imageUri,
                timestamp = timestamp
            )
            entryRepository.saveEntry(newEntry)
        }
    }
}