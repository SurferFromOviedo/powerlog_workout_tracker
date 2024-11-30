package com.example.workout_app_2.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSet(set: SetEntity): Long

    @Query("SELECT * FROM sets WHERE workoutId = :workoutId AND exerciseId = :exerciseId")
    suspend fun getSetsForExercise(workoutId: Int, exerciseId: Int): List<SetEntity>

    @Query("DELETE FROM sets WHERE workoutId = :workoutId")
    suspend fun deleteSetsByWorkoutId(workoutId: Int)

    @Query("SELECT * FROM sets WHERE workoutId = :workoutId")
    suspend fun getSetsByWorkoutId(workoutId: Int): List<SetEntity>

    @Query("SELECT * FROM sets")
    fun getAll(): Flow<List<SetEntity>>

    @Query("DELETE FROM sets")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSets(sets: List<SetEntity>)

}