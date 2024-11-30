package com.example.workout_app_2.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface TemplateDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplate(template: TemplateEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplateExercise(templateExercise: TemplateExerciseEntity)

    @Query("SELECT * FROM templates")
    suspend fun getAllTemplates(): List<TemplateEntity>

    @Query("SELECT name FROM templates")
    suspend fun getAllTemplateNames(): List<String>

    @Query("SELECT * FROM templates WHERE name = :name")
    suspend fun getTemplateByName(name: String): TemplateEntity?

    @Query("DELETE FROM template_exercises WHERE templateId = :templateId")
    suspend fun deleteTemplateExercisesByTemplateId(templateId: Int)

    @Query("DELETE FROM templates WHERE id = :templateId")
    suspend fun deleteTemplateByTemplateId(templateId: Int)

    @Transaction
    @Query("SELECT e.* \n" +
            "    FROM exercises e\n" +
            "    JOIN template_exercises te ON e.id = te.exerciseId\n" +
            "    WHERE te.templateId = :templateId\n" +
            "    ORDER BY te.orderIndex ASC")
    suspend fun getExercisesForTemplate(templateId: Int): List<ExerciseEntity>

    @Transaction
    @Query("SELECT COUNT(*) FROM template_exercises WHERE exerciseId = (SELECT id FROM exercises WHERE name = :exerciseName)")
    suspend fun getTemplateCountByExerciseName(exerciseName: String): Int

    @Query("SELECT * FROM templates")
    fun getAllTemplatesFlow(): Flow<List<TemplateEntity>>

    @Query("SELECT * FROM template_exercises")
    fun getAllTemplateExercises(): Flow<List<TemplateExerciseEntity>>

    @Query("DELETE FROM templates")
    suspend fun deleteAllTemplates()

    @Query("DELETE FROM template_exercises")
    suspend fun deleteAllTemplateExercises()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllTemplates(templates: List<TemplateEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllTemplateExercises(templateExercises: List<TemplateExerciseEntity>)

}