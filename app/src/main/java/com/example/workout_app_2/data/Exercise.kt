package com.example.workout_app_2.data

data class Exercise(
    var name: String,
    val bodyPart: String,
    val category: String,
    val image: String?,
    var sets: MutableList<ExerciseSet> = mutableListOf(),
    val id: String = "",
)

data class ExerciseSet(
    var weight: String = "",
    var unit: String? = null,
    var reps: String = ""
)

data class WorkoutTemplate(
    val name: String,
    val exercises: List<Exercise>
)