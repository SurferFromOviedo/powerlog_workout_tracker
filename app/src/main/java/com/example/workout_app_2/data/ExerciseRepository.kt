package com.example.workout_app_2.data

import android.content.Context

object ExerciseRepository{
    private val exercisesEntities = listOf(
        ExerciseEntity("Deadlift", "Whole Body", "Barbell", null),
        ExerciseEntity("Hex Bar Deadlift", "Whole Body", "Barbell", null),
        ExerciseEntity("Power Clean", "Whole Body", "Barbell", null),
        ExerciseEntity("Romanian Deadlift", "Whole Body", "Barbell", null),
        ExerciseEntity("Sumo Deadlift", "Whole Body", "Barbell", null),
        ExerciseEntity("Clean and Jerk", "Whole Body", "Barbell", null),
        ExerciseEntity("Snatch", "Whole Body", "Barbell", null),
        ExerciseEntity("Clean", "Whole Body", "Barbell", null),
        ExerciseEntity("Squat", "Legs", "Barbell", null),
        ExerciseEntity("Front Squat", "Legs", "Barbell", null),
        ExerciseEntity("Hip Thrust", "Legs", "Barbell", null),
        ExerciseEntity("Box Squat", "Legs", "Barbell", null),
        ExerciseEntity("Bulgarian Split Squat", "Legs", "Barbell", null),
        ExerciseEntity("Smith Machine Squat", "Legs", "Barbell", null),
        ExerciseEntity("Good Morning", "Legs", "Barbell", null),
        ExerciseEntity("Zercher Squat", "Legs", "Barbell", null),
        ExerciseEntity("Bent Over Row", "Back", "Barbell", null),
        ExerciseEntity("Barbell Shrug", "Back", "Barbell", null),
        ExerciseEntity("T Bar Row", "Back", "Barbell", null),
        ExerciseEntity("Pendlay Row", "Back", "Barbell", null),
        ExerciseEntity("Yates Row", "Back", "Barbell", null),
        ExerciseEntity("Bench Pull", "Back", "Barbell", null),
        ExerciseEntity("Barbell Pullover", "Back", "Barbell", null),
        ExerciseEntity("Hex Bar Shrug", "Back", "Barbell", null),
        ExerciseEntity("Bench Press", "Chest", "Barbell", null),
        ExerciseEntity("Incline Bench Press", "Chest", "Barbell", null),
        ExerciseEntity("Close Grip Bench Press", "Chest", "Barbell", null),
        ExerciseEntity("Decline Bench Press", "Chest", "Barbell", null),
        ExerciseEntity("Smith Machine Bench Press", "Chest", "Barbell", null),
        ExerciseEntity("Floor Press", "Chest", "Barbell", null),
        ExerciseEntity("Paused Bench Press", "Chest", "Barbell", null),
        ExerciseEntity("Reverse Grip Bench Press", "Chest", "Barbell", null),
        ExerciseEntity("Shoulder Press", "Shoulders", "Barbell", null),
        ExerciseEntity("Military Press", "Shoulders", "Barbell", null),
        ExerciseEntity("Seated Shoulder Press", "Shoulders", "Barbell", null),
        ExerciseEntity("Push Press", "Shoulders", "Barbell", null),
        ExerciseEntity("Upright Row", "Shoulders", "Barbell", null),
        ExerciseEntity("Neck Curl", "Shoulders", "Barbell", null),
        ExerciseEntity("Behind The Neck Press", "Shoulders", "Barbell", null),
        ExerciseEntity("Barbell Front Raise", "Shoulders", "Barbell", null),
        ExerciseEntity("Barbell Curl", "Biceps", "Barbell", null),
        ExerciseEntity("EZ Bar Curl", "Biceps", "Barbell", null),
        ExerciseEntity("Preacher Curl", "Biceps", "Barbell", null),
        ExerciseEntity("Strict Curl", "Biceps", "Barbell", null),
        ExerciseEntity("Spider Curl", "Biceps", "Barbell", null),
        ExerciseEntity("Cheat Curl", "Biceps", "Barbell", null),
        ExerciseEntity("Lying Tricep Extension", "Triceps", "Barbell", null),
        ExerciseEntity("Tricep Extension", "Triceps", "Barbell", null),
        ExerciseEntity("JM Press", "Triceps", "Barbell", null),
        ExerciseEntity("Wrist Curl", "Forearms", "Barbell", null),
        ExerciseEntity("Reverse Barbell Curl", "Forearms", "Barbell", null),
        ExerciseEntity("Reverse Wrist Curl", "Forearms", "Barbell", null),
        ExerciseEntity("Muscle Ups", "Whole Body", "Bodyweight", null),
        ExerciseEntity("Burpees", "Whole Body", "Bodyweight", null),
        ExerciseEntity("Ring Muscle Ups", "Whole Body", "Bodyweight", null),
        ExerciseEntity("Clap Pull Up", "Whole Body", "Bodyweight", null),
        ExerciseEntity("Squat Thrust", "Whole Body", "Bodyweight", null),
        ExerciseEntity("Bodyweight Squat", "Legs", "Bodyweight", null),
        ExerciseEntity("Single Leg Squat", "Legs", "Bodyweight", null),
        ExerciseEntity("Pistol Squat", "Legs", "Bodyweight", null),
        ExerciseEntity("Bodyweight Calf Raise", "Legs", "Bodyweight", null),
        ExerciseEntity("Lunge", "Legs", "Bodyweight", null),
        ExerciseEntity("Glute Bridge", "Legs", "Bodyweight", null),
        ExerciseEntity("Reverse Lunge", "Legs", "Bodyweight", null),
        ExerciseEntity("Squat Jump", "Legs", "Bodyweight", null),
        ExerciseEntity("Pull Ups", "Back", "Bodyweight", null),
        ExerciseEntity("Chin Ups", "Back", "Bodyweight", null),
        ExerciseEntity("Neutral Grip Pull Ups", "Back", "Bodyweight", null),
        ExerciseEntity("Back Extension", "Back", "Bodyweight", null),
        ExerciseEntity("One Arm Pull Ups", "Back", "Bodyweight", null),
        ExerciseEntity("Inverted Row", "Back", "Bodyweight", null),
        ExerciseEntity("Reverse Hyperextension", "Back", "Bodyweight", null),
        ExerciseEntity("Push Ups", "Chest", "Bodyweight", null),
        ExerciseEntity("One Arm Push Ups", "Chest", "Bodyweight", null),
        ExerciseEntity("Diamond Push Ups", "Chest", "Bodyweight", null),
        ExerciseEntity("Decline Push Up", "Chest", "Bodyweight", null),
        ExerciseEntity("Incline Push Up", "Chest", "Bodyweight", null),
        ExerciseEntity("Close Grip Push Up", "Chest", "Bodyweight", null),
        ExerciseEntity("Archer Push Ups", "Chest", "Bodyweight", null),
        ExerciseEntity("Handstand Push Ups", "Shoulders", "Bodyweight", null),
        ExerciseEntity("Pike Push Up", "Shoulders", "Bodyweight", null),
        ExerciseEntity("Dips", "Triceps", "Bodyweight", null),
        ExerciseEntity("Ring Dips", "Triceps", "Bodyweight", null),
        ExerciseEntity("Bench Dips", "Triceps", "Bodyweight", null),
        ExerciseEntity("Sit Ups", "Core", "Bodyweight", null),
        ExerciseEntity("Crunches", "Core", "Bodyweight", null),
        ExerciseEntity("Hanging Leg Raise", "Core", "Bodyweight", null),
        ExerciseEntity("Russian Twist", "Core", "Bodyweight", null),
        ExerciseEntity("Lying Leg Raise", "Core", "Bodyweight", null),
        ExerciseEntity("Decline Sit Up", "Core", "Bodyweight", null),
        ExerciseEntity("Hanging Knee Raise", "Core", "Bodyweight", null),
        ExerciseEntity("Ab Wheel Rollout", "Core", "Bodyweight", null),
        ExerciseEntity("Dumbbell Romanian Deadlift", "Whole Body", "Dumbbell", null),
        ExerciseEntity("Dumbbell Deadlift", "Whole Body", "Dumbbell", null),
        ExerciseEntity("Dumbbell Snatch", "Whole Body", "Dumbbell", null),
        ExerciseEntity("Single Leg Dumbbell Deadlift", "Whole Body", "Dumbbell", null),
        ExerciseEntity("Dumbbell Clean and Press", "Whole Body", "Dumbbell", null),
        ExerciseEntity("Dumbbell Thruster", "Whole Body", "Dumbbell", null),
        ExerciseEntity("Dumbbell High Pull", "Whole Body", "Dumbbell", null),
        ExerciseEntity("Dumbbell Hang Clean", "Whole Body", "Dumbbell", null),
        ExerciseEntity("Dumbbell Bulgarian Split Squat", "Legs", "Dumbbell", null),
        ExerciseEntity("Goblet Squat", "Legs", "Dumbbell", null),
        ExerciseEntity("Dumbbell Lunge", "Legs", "Dumbbell", null),
        ExerciseEntity("Dumbbell Squat", "Legs", "Dumbbell", null),
        ExerciseEntity("Dumbbell Calf Raise", "Legs", "Dumbbell", null),
        ExerciseEntity("Dumbbell Front Squat", "Legs", "Dumbbell", null),
        ExerciseEntity("Dumbbell Split Squat", "Legs", "Dumbbell", null),
        ExerciseEntity("Dumbbell Walking Calf Raise", "Legs", "Dumbbell", null),
        ExerciseEntity("Dumbbell Row", "Back", "Dumbbell", null),
        ExerciseEntity("Dumbbell Shrug", "Back", "Dumbbell", null),
        ExerciseEntity("Dumbbell Pullover", "Back", "Dumbbell", null),
        ExerciseEntity("Dumbbell Reverse Fly", "Back", "Dumbbell", null),
        ExerciseEntity("Chest Supported Dumbbell Row", "Back", "Dumbbell", null),
        ExerciseEntity("Bent Over Dumbbell Row", "Back", "Dumbbell", null),
        ExerciseEntity("Dumbbell Bench Pull", "Back", "Dumbbell", null),
        ExerciseEntity("Renegade Row", "Back", "Dumbbell", null),
        ExerciseEntity("Dumbbell Bench Press", "Chest", "Dumbbell", null),
        ExerciseEntity("Incline Dumbbell Bench Press", "Chest", "Dumbbell", null),
        ExerciseEntity("Dumbbell Fly", "Chest", "Dumbbell", null),
        ExerciseEntity("Incline Dumbbell Fly", "Chest", "Dumbbell", null),
        ExerciseEntity("Dumbbell Floor Press", "Chest", "Dumbbell", null),
        ExerciseEntity("Decline Dumbbell Bench Press", "Chest", "Dumbbell", null),
        ExerciseEntity("Close Grip Dumbbell Bench Press", "Chest", "Dumbbell", null),
        ExerciseEntity("Decline Dumbbell Fly", "Chest", "Dumbbell", null),
        ExerciseEntity("Dumbbell Shoulder Press", "Shoulders", "Dumbbell", null),
        ExerciseEntity("Dumbbell Lateral Raise", "Shoulders", "Dumbbell", null),
        ExerciseEntity("Seated Dumbbell Shoulder Press", "Shoulders", "Dumbbell", null),
        ExerciseEntity("Dumbbell Front Raise", "Shoulders", "Dumbbell", null),
        ExerciseEntity("Arnold Press", "Shoulders", "Dumbbell", null),
        ExerciseEntity("Dumbbell Upright Row", "Shoulders", "Dumbbell", null),
        ExerciseEntity("Dumbbell Z Press", "Shoulders", "Dumbbell", null),
        ExerciseEntity("Dumbbell External Rotation", "Shoulders", "Dumbbell", null),
        ExerciseEntity("Dumbbell Curl", "Biceps", "Dumbbell", null),
        ExerciseEntity("Hammer Curl", "Biceps", "Dumbbell", null),
        ExerciseEntity("Dumbbell Concentration Curl", "Biceps", "Dumbbell", null),
        ExerciseEntity("Incline Dumbbell Curl", "Biceps", "Dumbbell", null),
        ExerciseEntity("One Arm Dumbbell Preacher Curl", "Biceps", "Dumbbell", null),
        ExerciseEntity("Incline Hammer Curl", "Biceps", "Dumbbell", null),
        ExerciseEntity("Zottman Curl", "Biceps", "Dumbbell", null),
        ExerciseEntity("Seated Dumbbell Curl", "Biceps", "Dumbbell", null),
        ExerciseEntity("Dumbbell Tricep Extension", "Triceps", "Dumbbell", null),
        ExerciseEntity("Lying Dumbbell Tricep Extension", "Triceps", "Dumbbell", null),
        ExerciseEntity("Dumbbell Tricep Kickback", "Triceps", "Dumbbell", null),
        ExerciseEntity("Seated Dumbbell Tricep Extension", "Triceps", "Dumbbell", null),
        ExerciseEntity("Tate Press", "Triceps", "Dumbbell", null),
        ExerciseEntity("Dumbbell Side Bend", "Core", "Dumbbell", null),
        ExerciseEntity("Dumbbell Wrist Curl", "Forearms", "Dumbbell", null),
        ExerciseEntity("Dumbbell Reverse Wrist Curl", "Forearms", "Dumbbell", null),
        ExerciseEntity("Dumbbell Reverse Curl", "Forearms", "Dumbbell", null),
        ExerciseEntity("Sled Leg Press", "Legs", "Machine", null),
        ExerciseEntity("Leg Extension", "Legs", "Machine", null),
        ExerciseEntity("Horizontal Leg Press", "Legs", "Machine", null),
        ExerciseEntity("Hack Squat", "Legs", "Machine", null),
        ExerciseEntity("Seated Leg Curl", "Legs", "Machine", null),
        ExerciseEntity("Lying Leg Curl", "Legs", "Machine", null),
        ExerciseEntity("Machine Calf Raise", "Legs", "Machine", null),
        ExerciseEntity("Vertical Leg Press", "Legs", "Machine", null),
        ExerciseEntity("Machine Row", "Back", "Machine", null),
        ExerciseEntity("Machine Reverse Fly", "Back", "Machine", null),
        ExerciseEntity("Machine Back Extension", "Back", "Machine", null),
        ExerciseEntity("Machine Shrug", "Back", "Machine", null),
        ExerciseEntity("Chest Press", "Chest", "Machine", null),
        ExerciseEntity("Machine Chest Fly", "Chest", "Machine", null),
        ExerciseEntity("Machine Shoulder Press", "Shoulders", "Machine", null),
        ExerciseEntity("Machine Lateral Raise", "Shoulders", "Machine", null),
        ExerciseEntity("Machine Bicep Curl", "Biceps", "Machine", null),
        ExerciseEntity("Seated Dip Machine", "Triceps", "Machine", null),
        ExerciseEntity("Machine Tricep Extension", "Triceps", "Machine", null),
        ExerciseEntity("Machine Seated Crunch", "Core", "Machine", null),
        ExerciseEntity("Cable Pull Through", "Legs", "Cable", null),
        ExerciseEntity("Cable Kickback", "Legs", "Cable", null),
        ExerciseEntity("Cable Leg Extension", "Legs", "Cable", null),
        ExerciseEntity("Lat Pulldown", "Back", "Cable", null),
        ExerciseEntity("Seated Cable Row", "Back", "Cable", null),
        ExerciseEntity("Close Grip Lat Pulldown", "Back", "Cable", null),
        ExerciseEntity("Reverse Grip Lat Pulldown", "Back", "Cable", null),
        ExerciseEntity("Straight Arm Pulldown", "Back", "Cable", null),
        ExerciseEntity("Cable Reverse Fly", "Back", "Cable", null),
        ExerciseEntity("One Arm Lat Pulldown", "Back", "Cable", null),
        ExerciseEntity("One Arm Seated Cable Row", "Back", "Cable", null),
        ExerciseEntity("Cable Fly", "Chest", "Cable", null),
        ExerciseEntity("Cable Lateral Raise", "Shoulders", "Cable", null),
        ExerciseEntity("Face Pull", "Shoulders", "Cable", null),
        ExerciseEntity("Cable External Rotation", "Shoulders", "Cable", null),
        ExerciseEntity("Cable Bicep Curl", "Biceps", "Cable", null),
        ExerciseEntity("One Arm Cable Bicep Curl", "Biceps", "Cable", null),
        ExerciseEntity("Cable Hammer Curl", "Biceps", "Cable", null),
        ExerciseEntity("One Arm Pulldown", "Biceps", "Cable", null),
        ExerciseEntity("Overhead Cable Curl", "Biceps", "Cable", null),
        ExerciseEntity("Incline Cable Curl", "Biceps", "Cable", null),
        ExerciseEntity("Lying Cable Curl", "Biceps", "Cable", null),
        ExerciseEntity("Tricep Pushdown", "Triceps", "Cable", null),
        ExerciseEntity("Tricep Rope Pushdown", "Triceps", "Cable", null),
        ExerciseEntity("Cable Overhead Tricep Extension", "Triceps", "Cable", null),
        ExerciseEntity("Reverse Grip Tricep Pushdown", "Triceps", "Cable", null),
        ExerciseEntity("Cable Crunch", "Core", "Cable", null),
        ExerciseEntity("Cable Woodchopper", "Core", "Cable", null),
        ExerciseEntity("High Pulley Crunch", "Core", "Cable", null),
        ExerciseEntity("Standing Cable Crunch", "Core", "Cable", null)
    )

    suspend fun initializeExercises(context: Context){
        val database = DatabaseProvider.getDatabase(context)
        val exerciseDao = database.exerciseDao()

        if (exerciseDao.getExerciseCount() == 0) {
            exerciseDao.insertExercises(exercisesEntities)
        }
    }

    private suspend fun getExercises(context: Context): List<ExerciseEntity> {
        val database = DatabaseProvider.getDatabase(context)
        return database.exerciseDao().getAllExercises()
    }

    suspend fun getExercisesMapped(context: Context): List<Exercise> {
        val exerciseEntities = getExercises(context)
        val sortedExerciseEntities = exerciseEntities.sortedByDescending {it.usage}
        return sortedExerciseEntities.map { exerciseEntity ->
            Exercise(
                id = exerciseEntity.id.toString(),
                name = exerciseEntity.name,
                bodyPart = exerciseEntity.bodyPart,
                category = exerciseEntity.category,
                image = exerciseEntity.image
            )
        }
    }


}