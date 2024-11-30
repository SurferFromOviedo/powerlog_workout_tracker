package com.example.workout_app_2.presentation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.workout_app_2.data.Exercise
import com.example.workout_app_2.data.ExerciseSet
import com.example.workout_app_2.data.PreferencesDataStore
import com.example.workout_app_2.data.PreferencesViewModel
import com.example.workout_app_2.ui.theme.Workout_App_2Theme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SelectedExerciseItem(
    exercise: Exercise,
    preferencesViewModel: PreferencesViewModel,
    onDelete: () -> Unit,
    onExerciseChange: (Exercise) -> Unit
){
    var sets by remember { mutableStateOf(exercise.sets.ifEmpty { mutableListOf(ExerciseSet()) }) }
    val updatedExercise by rememberUpdatedState(exercise.copy(sets = sets))
    val hapticFeedback = LocalHapticFeedback.current

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
                .combinedClickable(
                    onClick = {
                        sets = (sets + ExerciseSet()).toMutableList()
                              },
                    onLongClick = {
                        if (sets.size > 1) {
                            sets = sets.dropLast(1).toMutableList()
                        } else {
                            onDelete()
                        }
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    }
                )
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 16.dp),
                text = exercise.name,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            sets.forEachIndexed { index, set ->
                SetItem(
                    set = set,
                    preferencesViewModel = preferencesViewModel,
                    onSetChange = { updatedSet ->
                        sets = sets.toMutableList().also { it[index] = updatedSet
                        }
                    }
                )
            }
        }
    }

    exercise.sets = sets
}



@Preview(showBackground = true, showSystemUi = false)
@Composable
fun PreviewSelectedExerciseItem() {
    val fakeExercise = Exercise("Deadlift", "Privet", "Kad dela",null)
    Workout_App_2Theme {
       // SelectedExerciseItem(fakeExercise,PreferencesViewModel(PreferencesDataStore(LocalContext.current)), onDelete = {}, onExerciseChange = {})
    }
}