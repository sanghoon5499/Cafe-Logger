package com.example.cafelogger.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.cafelogger.repository.EntryRepository

class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val entryRepository = EntryRepository(context.applicationContext)

        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(entryRepository) as T
            }
            modelClass.isAssignableFrom(UploadViewModel::class.java) -> {
                UploadViewModel(entryRepository) as T
            }

            else -> {
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}