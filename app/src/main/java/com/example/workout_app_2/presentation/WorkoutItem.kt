package com.example.workout_app_2.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.example.workout_app_2.data.DatabaseProvider
import com.example.workout_app_2.data.Exercise
import com.example.workout_app_2.data.ExerciseEntity
import com.example.workout_app_2.data.WorkoutEntity
import com.example.workout_app_2.ui.theme.Workout_App_2Theme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@Composable
fun WorkoutItem(
    workoutEntity: WorkoutEntity,
    onClick: () -> Unit
){
    val context = LocalContext.current
    val database = remember { DatabaseProvider.getDatabase(context) }
    val exercises = remember { mutableStateListOf<ExerciseEntity>()}

    val startTime = workoutEntity.startTime
    val endTime = workoutEntity.duration + startTime
    val spf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    spf.timeZone = TimeZone.getTimeZone("UTC")
    val startTimeFormatted = spf.format(Date(startTime))
    val endTimeFormatted = spf.format(Date(endTime))

    val bodyPartsWithoutDuplicates = exercises.map { it.bodyPart }.distinct()
    val exerciseNamesWithoutDuplicates = exercises.map { it.name }.distinct()
    val bodyPartsString = bodyPartsWithoutDuplicates.joinToString(", ")
    val exerciseNamesString = exerciseNamesWithoutDuplicates.joinToString(", ")

    LaunchedEffect(Unit) {
        exercises.addAll(database.workoutDao().getExercisesForWorkout(workoutEntity.id))
    }

    Surface(modifier = Modifier
        .height(105.dp)
        .fillMaxWidth()
        .padding(vertical = 6.dp, horizontal = 14.dp),
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 4.dp)
    {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary.copy(0.2f), RoundedCornerShape(16.dp))
                .clickable { onClick() },
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Text(
                modifier = Modifier
                    .padding(bottom = 2.dp, top = 8.dp),
                text = "$startTimeFormatted - $endTimeFormatted",
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                modifier = Modifier.padding(horizontal = 8.dp),
                text = bodyPartsString,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(0.8f)
            )
            Text(
                modifier = Modifier.padding(horizontal = 8.dp),
                text = exerciseNamesString,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(0.8f)
            )
        }
    }
}

@Preview
@Composable
fun WorkoutItemPreview(){
    Workout_App_2Theme {
        WorkoutItem(workoutEntity = WorkoutEntity(1728819372496, 122956, 5), onClick = {})
    }
}