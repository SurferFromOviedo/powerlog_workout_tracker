package com.example.workout_app_2.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class PreferencesViewModelFactory(
    private val preferencesDataStore: PreferencesDataStore
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PreferencesViewModel::class.java)) {
            return PreferencesViewModel(preferencesDataStore) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
