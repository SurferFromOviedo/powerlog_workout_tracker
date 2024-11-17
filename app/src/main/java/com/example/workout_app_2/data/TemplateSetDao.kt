package com.example.workout_app_2.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TemplateSetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplateSet(set: TemplateSetEntity): Long

    @Query("SELECT * FROM template_sets WHERE templateId = :templateId AND exerciseId = :exerciseId")
    suspend fun getSetsForExercise(templateId: Int, exerciseId: Int): List<TemplateSetEntity>

    @Query("DELETE FROM template_sets WHERE templateId = :templateId")
    suspend fun deleteTemplateSetsByTemplateId(templateId: Int)
}