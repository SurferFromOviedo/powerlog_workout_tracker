package com.example.workout_app_2.data

import android.content.Context
import android.content.res.Resources
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.map

class PreferencesDataStore(context: Context) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "workout_preferences")
    private val dataStore = context.dataStore

    private val START_TIME_KEY = longPreferencesKey("start_time_key")
    private val ACCUMULATED_TIME_KEY = longPreferencesKey("accumulated_time_key")
    private val IS_RUNNING_KEY = booleanPreferencesKey("is_running_key")
    private val EXERCISE_KEY = stringPreferencesKey("exercise_key")
    private val UNIT_KEY = stringPreferencesKey("unit_key")
    private val THEME_KEY = stringPreferencesKey("theme_key")
    private val DYNAMIC_COLOR_KEY = booleanPreferencesKey("dynamic_color_key")
    private val PRIMARY_COLOR_KEY = intPreferencesKey("primary_color_key")
    private val SCREEN_ON_KEY = booleanPreferencesKey("screen_on_key")

    val screenOnFlow = dataStore.data.map { preferences ->
        preferences[SCREEN_ON_KEY] ?: false
    }

    val startTimeFlow = dataStore.data.map { preferences ->
        preferences[START_TIME_KEY] ?: 0L
    }

    val accumulatedTimeFlow = dataStore.data.map { preferences ->
        preferences[ACCUMULATED_TIME_KEY] ?: 0L
    }

    val isRunningFlow = dataStore.data.map { preferences ->
        preferences[IS_RUNNING_KEY] ?: false
    }

    val exerciseFlow = dataStore.data.map { preferences ->
        val exerciseJson = preferences[EXERCISE_KEY]
        exerciseJson?.let {
            Gson().fromJson<List<Exercise>>(it, object : TypeToken<List<Exercise>>() {}.type)
        } ?: emptyList()
    }

    val unitFlow = dataStore.data.map { preferences ->
        preferences[UNIT_KEY] ?: "KG"
    }

    val themeFlow = dataStore.data.map { preferences ->
        preferences[THEME_KEY] ?: "Default"
    }

    val dynamicColorFlow = dataStore.data.map { preferences ->
        preferences[DYNAMIC_COLOR_KEY] ?: true
    }

    val primaryColorFlow = dataStore.data.map { preferences ->
        preferences[PRIMARY_COLOR_KEY] ?: Color(0xFF6650a4).toArgb()
    }



    suspend fun saveScreenOn(screenOn: Boolean) {
        dataStore.edit { preferences ->
            preferences[SCREEN_ON_KEY] = screenOn
        }
    }

    suspend fun saveDynamicColor(dynamicColor: Boolean) {
        dataStore.edit { preferences ->
            preferences[DYNAMIC_COLOR_KEY] = dynamicColor
        }
    }

    suspend fun savePrimaryColor(color: Color) {
        dataStore.edit { preferences ->
            preferences[PRIMARY_COLOR_KEY] = color.toArgb()
        }
    }


    suspend fun saveTheme(theme: String) {
        dataStore.edit { preferences ->
            preferences[THEME_KEY] = theme
        }
    }

    suspend fun saveUnit(unit: String) {
        dataStore.edit { preferences ->
            preferences[UNIT_KEY] = unit
        }
    }

    suspend fun saveTimer(startTime: Long, accumulatedTime: Long, isRunning: Boolean) {
        dataStore.edit { preferences ->
            preferences[START_TIME_KEY] = startTime
            preferences[ACCUMULATED_TIME_KEY] = accumulatedTime
            preferences[IS_RUNNING_KEY] = isRunning
        }
    }

    suspend fun saveExercises(exercises: List<Exercise>) {
        val exerciseJson = Gson().toJson(exercises)
        dataStore.edit { preferences ->
            preferences[EXERCISE_KEY] = exerciseJson
        }
    }

}
