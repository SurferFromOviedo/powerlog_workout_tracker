package com.example.workout_app_2.data

import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkout(workout: WorkoutEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutExercise(workoutExercise: WorkoutExerciseEntity)

    @Transaction
    @Query("SELECT COUNT(*) FROM workout_exercises WHERE exerciseId = (SELECT id FROM exercises WHERE name = :exerciseName)")
    suspend fun getWorkoutCountByExerciseName(exerciseName: String): Int

    @Transaction
    @Query("SELECT * FROM workouts")
    suspend fun getAllWorkouts(): List<WorkoutEntity>

    @Transaction
    @Query("SELECT * FROM workouts WHERE startTime BETWEEN :startTimestamp AND :endTimestamp")
    suspend fun getAllWorkoutsBetweenTimestamps(startTimestamp: Long, endTimestamp: Long): List<WorkoutEntity>

    @Transaction
    @Query("SELECT startTime FROM workouts WHERE id = :workoutId")
    suspend fun getStartTimeByWorkoutId(workoutId: Int): Long

    @Transaction
    @Query("SELECT * FROM workout_exercises")
    suspend fun getAllWorkoutExercises(): List<WorkoutExerciseEntity>

    @Transaction
    @Query("SELECT * FROM workout_exercises WHERE workoutId in (SELECT id FROM workouts WHERE startTime BETWEEN :startTimestamp AND :endTimestamp)")
    suspend fun getAllWorkoutExercisesBetweenTimestamps(startTimestamp: Long, endTimestamp: Long): List<WorkoutExerciseEntity>

    @Transaction
    @Query("SELECT e.* \n" +
            "    FROM exercises e\n" +
            "    JOIN workout_exercises we ON e.id = we.exerciseId\n" +
            "    WHERE we.workoutId = :workoutId\n" +
            "    ORDER BY we.orderIndex ASC")
    suspend fun getExercisesForWorkout(workoutId: Int): List<ExerciseEntity>

    @Query("DELETE FROM workout_exercises WHERE workoutId = :workoutId")
    suspend fun deleteWorkoutExercisesById(workoutId: Int)

    @Query("DELETE FROM workouts WHERE id = :workoutId")
    suspend fun deleteWorkoutById(workoutId: Int)

    @Query("SELECT * FROM workouts WHERE id = :workoutId")
    suspend fun getWorkoutById(workoutId: Int): WorkoutEntity?

    @Query("SELECT * FROM workouts")
    fun getAllWorkouts2(): Flow<List<WorkoutEntity>>

    @Query("SELECT * FROM workout_exercises")
    fun getAllWorkoutsExercises(): Flow<List<WorkoutExerciseEntity>>

    @Query("DELETE FROM workouts")
    suspend fun deleteAllWorkouts()

    @Query("DELETE FROM workout_exercises")
    suspend fun deleteAllWorkoutsExercises()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllWorkoutExercises(workoutExercises: List<WorkoutExerciseEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllWorkouts(workouts: List<WorkoutEntity>)

}