package com.example.workout_app_2.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.workout_app_2.data.DatabaseProvider
import com.example.workout_app_2.data.Exercise
import com.example.workout_app_2.data.WorkoutTemplate
import com.example.workout_app_2.ui.theme.Workout_App_2Theme

@Composable
fun TemplateSelectorItem(
    template: WorkoutTemplate,
    onClick: (WorkoutTemplate) -> Unit
){
    val exerciseList = template.exercises
    val exercisesNamesString = exerciseList.joinToString(", ") { it.name }

    val context = LocalContext.current
    Surface(modifier = Modifier
        .height(105.dp)
        .fillMaxWidth()
        .padding(vertical = 6.dp),
        shape = RoundedCornerShape(16.dp))
    {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick(WorkoutTemplate(template.name, exerciseList)) }
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
                    text = template.name,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    modifier = Modifier.padding(),
                    text = exercisesNamesString,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(0.8f)
                )

            }



        }
    }
}

@Preview(showBackground = true, showSystemUi = false)
@Composable
fun PreviewTemplateSelectorItem(){
    val sampleTemplate = WorkoutTemplate(
        name = "Sample Template",
        exercises = List(1){
            Exercise(
                name = "hello",
                bodyPart = "legs",
                category = "barbell",
                image = null
            )
        }
    )
    Workout_App_2Theme {
        TemplateSelectorItem(template = sampleTemplate) {
        }
    }
}