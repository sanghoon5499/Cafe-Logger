package com.example.cafelogger.viewmodel

import androidx.lifecycle.ViewModel
import com.example.cafelogger.model.Entry
import com.example.cafelogger.repository.EntryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DetailsViewModel(private val entryRepository: EntryRepository) : ViewModel() {
    private val _entry = MutableStateFlow<Entry?>(null)
    val entry: StateFlow<Entry?> = _entry

    fun selectEntry(entry: Entry) {
        _entry.value = entry
    }

    fun deleteEntry(entry: Entry) {
        entryRepository.deleteEntry(entry)
    }
}