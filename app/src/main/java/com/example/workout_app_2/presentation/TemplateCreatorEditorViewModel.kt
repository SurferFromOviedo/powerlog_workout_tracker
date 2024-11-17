package com.example.workout_app_2.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.workout_app_2.data.Exercise

class TemplateCreatorEditorViewModel : ViewModel() {

    var selectedExercises = mutableStateListOf<Exercise>()

}