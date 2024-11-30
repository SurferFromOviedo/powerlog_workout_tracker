package com.example.workout_app_2.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey

@Entity(tableName = "exercises")
data class ExerciseEntity(
    val name: String,
    val bodyPart: String,
    val category: String,
    val image: String? = null,
    val usage: Int = 0,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
){
    constructor() : this("", "", "", null, 0, 0)
}

@Entity(tableName = "workouts")
data class WorkoutEntity(
    val startTime: Long,
    val duration: Long,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
){
    constructor() : this(0, 0, 0)
}

@Entity(tableName = "templates")
data class TemplateEntity(
    var name: String,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
){
    constructor() : this("", 0)
}

@Entity(
    tableName = "workout_exercises",
    foreignKeys = [
    ForeignKey(entity = WorkoutEntity::class, parentColumns = ["id"], childColumns = ["workoutId"]),
    ForeignKey(entity = ExerciseEntity::class, parentColumns = ["id"], childColumns = ["exerciseId"])
    ]
)
data class WorkoutExerciseEntity(
    val workoutId: Int,
    val exerciseId: Int,
    val orderIndex: Int,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
){
    constructor() : this(0, 0, 0, 0)
}

@Entity(
    tableName = "template_exercises",
    foreignKeys = [
        ForeignKey(entity = TemplateEntity::class, parentColumns = ["id"], childColumns = ["templateId"]),
        ForeignKey(entity = ExerciseEntity::class, parentColumns = ["id"], childColumns = ["exerciseId"])
    ]
)
data class TemplateExerciseEntity(
    val templateId: Int,
    val exerciseId: Int,
    val orderIndex: Int,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
){
    constructor() : this(0, 0, 0, 0)
}

@Entity(tableName = "template_sets")
data class TemplateSetEntity(
    val weight: String,
    val unit: String,
    val reps: String,
    val exerciseId: Int,
    val templateId: Int,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
){
    constructor() : this("", "", "", 0, 0, 0)
}


@Entity(tableName = "sets")
data class SetEntity(
    val weight: String,
    val unit: String,
    val reps: String,
    val exerciseId: Int,
    val workoutId: Int,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
){
    constructor() : this("", "", "", 0, 0, 0)
}
