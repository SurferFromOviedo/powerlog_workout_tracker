package com.example.workout_app_2.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class PreferencesViewModelFactory(
    private val preferencesDataStore: PreferencesDataStore,
    private val firebaseRepository: FirebaseRepository,
    private val exerciseDao: ExerciseDao,
    private val workoutDao: WorkoutDao,
    private val templateDao: TemplateDao,
    private val templateSetDao: TemplateSetDao,
    private val setDao: SetDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PreferencesViewModel::class.java)) {
            return PreferencesViewModel(preferencesDataStore, firebaseRepository, exerciseDao, workoutDao, templateDao, templateSetDao, setDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
