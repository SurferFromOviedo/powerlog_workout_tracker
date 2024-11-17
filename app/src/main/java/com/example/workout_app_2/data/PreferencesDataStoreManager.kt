package com.example.workout_app_2.data

import android.content.Context

object PreferencesDataStoreManager {

    private lateinit var preferencesDataStore: PreferencesDataStore

    fun getInstance(context: Context): PreferencesDataStore {
        if (!PreferencesDataStoreManager::preferencesDataStore.isInitialized) {
            preferencesDataStore = PreferencesDataStore(context)
        }
        return preferencesDataStore
    }
}