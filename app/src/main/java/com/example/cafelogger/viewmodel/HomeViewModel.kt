package com.example.cafelogger.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cafelogger.model.Entry
import com.example.cafelogger.repository.EntryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private val entryRepository: EntryRepository) : ViewModel() {
    private val _recents = MutableStateFlow<List<Entry>>(emptyList())
    val recents: StateFlow<List<Entry>> = _recents

    init {
        fetchRecents()
    }

    private fun fetchRecents() {
        viewModelScope.launch {
            _recents.value = entryRepository.getEntries()
        }
    }
}