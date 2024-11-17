package com.example.workout_app_2.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.workout_app_2.data.Exercise
import com.example.workout_app_2.data.ExerciseSet
import com.example.workout_app_2.ui.theme.Workout_App_2Theme

@Composable
fun SelectedExerciseItemForCalendarSheet(
    exercise: Exercise,
    unit: String?,
    onExerciseChange: (Exercise) -> Unit
){
    val sets by remember { mutableStateOf(exercise.sets.ifEmpty { mutableListOf(ExerciseSet()) }) }
    val updatedExercise by rememberUpdatedState(exercise.copy(sets = sets))

    LaunchedEffect(sets) {
        onExerciseChange(updatedExercise)
    }

    Surface(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary.copy(0.2f))
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 16.dp),
                text = exercise.name,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            sets.forEach { set ->
                SetItemForCalendarSheet(
                    set = set,
                    unitString = unit,
                )
            }
        }
    }

    exercise.sets = sets
}

@Preview(showBackground = true, showSystemUi = false)
@Composable
fun PreviewSelectedExerciseItemForCalendarSheet() {
    val fakeExercise = Exercise("Deadlift", "Privet", "Kad dela",null)
    Workout_App_2Theme {
        SelectedExerciseItemForCalendarSheet(fakeExercise, unit = "KG", onExerciseChange = {})
    }
}