package com.example.workout_app_2.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.workout_app_2.data.Exercise
import com.example.workout_app_2.ui.theme.Workout_App_2Theme

@Composable
fun ExerciseSelectorItem(
    exercise: Exercise,
    onClick: () -> Unit
){
    Surface(modifier = Modifier
        .height(105.dp)
        .fillMaxWidth()
        .padding(vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 4.dp)
    {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .background(MaterialTheme.colorScheme.primary.copy(0.2f), RoundedCornerShape(16.dp)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
            ) {
                Text(
                    modifier = Modifier.padding(bottom = 2.dp),
                    text = exercise.name,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    modifier = Modifier.padding(),
                    text = exercise.bodyPart,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(0.8f)
                )
                Text(
                    modifier = Modifier.padding(),
                    text = exercise.category,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(0.8f)
                )
            }

            if (exercise.image != null){
                Image(
                    painter = painterResource(id = exercise.image.toInt()),
                    contentDescription = exercise.name,
                    modifier = Modifier
                        .size(100.dp)
                        .padding(8.dp)
                )
            }

        }
    }
}

@Preview(showBackground = true, showSystemUi = false)
@Composable
fun PreviewExerciseSelectorItem(){
    val sampleExercise = Exercise(
        name = "Deadlift",
        bodyPart = "Whole Body",
        category = "Barbell",
        image = null
    )
    Workout_App_2Theme {
        ExerciseSelectorItem(exercise = sampleExercise, onClick = {})
    }
}