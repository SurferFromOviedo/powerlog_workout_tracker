package com.example.workout_app_2.data

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PreferencesViewModel(
    private val preferencesDataStore: PreferencesDataStore
) : ViewModel(){

    val statsExercise = mutableStateOf<Exercise?>(null)

    val startTimeFlow: StateFlow<Long> = preferencesDataStore.startTimeFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    val accumulatedTimeFlow: StateFlow<Long> = preferencesDataStore.accumulatedTimeFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    val isRunningFlow: StateFlow<Boolean> = preferencesDataStore.isRunningFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val exerciseFlow: StateFlow<List<Exercise>> = preferencesDataStore.exerciseFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unitFlow: StateFlow<String> = preferencesDataStore.unitFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "KG")

    val themeFlow: StateFlow<String> = preferencesDataStore.themeFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Default")

    val dynamicColorFlow: StateFlow<Boolean> = preferencesDataStore.dynamicColorFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val primaryColorFlow: StateFlow<Int> = preferencesDataStore.primaryColorFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Color(0xFF6650a4).toArgb())

    val screenOnFlow: StateFlow<Boolean> = preferencesDataStore.screenOnFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun saveScreenOn(screenOn: Boolean) {
        viewModelScope.launch {
            preferencesDataStore.saveScreenOn(screenOn)
        }
    }

    fun saveDynamicColor(dynamicColor: Boolean) {
        viewModelScope.launch {
            preferencesDataStore.saveDynamicColor(dynamicColor)
        }
    }

    fun savePrimaryColor(primaryColor: Color) {
        viewModelScope.launch {
            preferencesDataStore.savePrimaryColor(primaryColor)
        }
    }

    fun saveTheme(theme: String) {
        viewModelScope.launch {
            preferencesDataStore.saveTheme(theme)
        }
    }

    fun saveUnit(unit: String) {
        viewModelScope.launch {
            preferencesDataStore.saveUnit(unit)
        }
    }

    fun saveTimer(startTime: Long, accumulatedTime: Long, isRunning: Boolean) {
        viewModelScope.launch {
            preferencesDataStore.saveTimer(startTime, accumulatedTime, isRunning)
        }
    }

    fun saveExercises(exercises: List<Exercise>) {
        viewModelScope.launch {
            preferencesDataStore.saveExercises(exercises)
        }
    }
}