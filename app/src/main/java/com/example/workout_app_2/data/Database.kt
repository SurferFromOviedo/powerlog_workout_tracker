package com.example.workout_app_2.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ExerciseEntity::class, WorkoutEntity::class, WorkoutExerciseEntity::class, SetEntity::class, TemplateEntity::class, TemplateExerciseEntity::class, TemplateSetEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase(){
    abstract fun exerciseDao(): ExerciseDao
    abstract fun workoutDao(): WorkoutDao
    abstract fun setDao(): SetDao
    abstract fun templateDao(): TemplateDao
    abstract fun templateSetDao(): TemplateSetDao

}