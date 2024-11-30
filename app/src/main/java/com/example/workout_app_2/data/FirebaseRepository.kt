package com.example.workout_app_2.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun getUserDocument(): DocumentReference? {
        val uid = auth.currentUser?.uid
        if (uid.isNullOrEmpty()) {
            return null
        }
        return firestore.collection("users").document(uid)
    }

    suspend fun uploadData(
        exercises: ExerciseEntity?,
        sets: SetEntity?,
        templateExercises: TemplateExerciseEntity?,
        templateSets: TemplateSetEntity?,
        templates: TemplateEntity?,
        workoutExercises: WorkoutExerciseEntity?,
        workouts: WorkoutEntity?
    ) {

        val userDocument = getUserDocument()?: return

        if (exercises != null) {
            userDocument.collection("exercises").document(exercises.id.toString()).set(exercises).await()
        }
        if (sets != null) {
            userDocument.collection("sets").document(sets.id.toString()).set(sets).await()
        }
        if (templateExercises != null) {
            userDocument.collection("template_exercises").document(templateExercises.id.toString()).set(templateExercises).await()
        }
        if (templateSets != null) {
            userDocument.collection("template_sets").document(templateSets.id.toString()).set(templateSets).await()
        }
        if (templates != null) {
            userDocument.collection("templates").document(templates.id.toString()).set(templates).await()
        }
        if (workoutExercises != null) {
            userDocument.collection("workout_exercises").document(workoutExercises.id.toString()).set(workoutExercises).await()
        }
        if (workouts != null) {
            userDocument.collection("workouts").document(workouts.id.toString()).set(workouts).await()
        }
    }

    suspend fun deleteDataById(id: Int, table: String) {
        val userDocument = getUserDocument()?: return

        if (table == "exercises") {
            userDocument.collection("exercises").document(id.toString()).delete().await()
        }
        if (table == "sets") {
            userDocument.collection("sets").document(id.toString()).delete().await()
        }
        if (table == "template_exercises") {
            userDocument.collection("template_exercises").document(id.toString()).delete().await()
        }
        if (table == "template_sets") {
            userDocument.collection("template_sets").document(id.toString()).delete().await()
        }
        if (table == "templates") {
            userDocument.collection("templates").document(id.toString()).delete().await()
        }
        if (table == "workout_exercises") {
            userDocument.collection("workout_exercises").document(id.toString()).delete().await()
        }
        if (table == "workouts") {
            userDocument.collection("workouts").document(id.toString()).delete().await()
        }
    }

    suspend fun hasDataInFirestore(): Boolean {
        val userDocument = getUserDocument()?: return false
        val exercises = userDocument.collection("exercises").get().await()
        val sets = userDocument.collection("sets").get().await()
        val templateExercises = userDocument.collection("template_exercises").get().await()
        val templateSets = userDocument.collection("template_sets").get().await()
        val templates = userDocument.collection("templates").get().await()
        val workoutExercises = userDocument.collection("workout_exercises").get().await()
        val workouts = userDocument.collection("workouts").get().await()

        return !(exercises.isEmpty && sets.isEmpty && templateExercises.isEmpty && templateSets.isEmpty && templates.isEmpty && workoutExercises.isEmpty && workouts.isEmpty)
    }
}