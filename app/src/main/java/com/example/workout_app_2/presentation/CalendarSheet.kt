package com.example.workout_app_2.presentation

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.workout_app_2.data.DatabaseProvider
import com.example.workout_app_2.data.Exercise
import com.example.workout_app_2.data.ExerciseEntity
import com.example.workout_app_2.data.ExerciseRepository
import com.example.workout_app_2.data.ExerciseSet
import com.example.workout_app_2.data.SetEntity
import com.example.workout_app_2.data.WorkoutEntity
import com.example.workout_app_2.data.WorkoutExerciseEntity
import com.example.workout_app_2.ui.theme.Workout_App_2Theme
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.UUID


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarSheet(
    workoutEntities: List<WorkoutEntity>,
    onDismissRequest: () -> Unit,
    onWorkoutDelete: () -> Unit,
    onWorkoutEdit: () -> Unit,
    unit: String,
    theme: String,
    dynamicColor: Boolean,
    primaryColor: Int,
    screenOn: Boolean,
){
    val sheetState = rememberModalBottomSheetState()
    val context = LocalContext.current
    val database = remember { DatabaseProvider.getDatabase(context) }
    val scope = rememberCoroutineScope()
    val showAlert = remember { mutableStateOf(false) }
    val exercises = remember { mutableStateListOf<Exercise>() }
    val selectedWorkoutEntity = remember { mutableStateOf<WorkoutEntity?>(null) }
    val showWorkout = remember { mutableStateOf(false) }
    val isLoading = remember { mutableStateOf(false) }
    val smthChanged = remember { mutableStateOf(false) }

    LaunchedEffect(workoutEntities) {
        if (workoutEntities.size == 1){
            selectedWorkoutEntity.value = workoutEntities[0]
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ){ result ->
        if(result.resultCode == Activity.RESULT_OK) {
            scope.launch {
                onWorkoutEdit()
                smthChanged.value = !smthChanged.value
            }
        }
    }

    LaunchedEffect(selectedWorkoutEntity.value, smthChanged.value) {
        exercises.clear()
        if (selectedWorkoutEntity.value != null) {
            isLoading.value = true
            val exercisesForWorkout = database.workoutDao().getExercisesForWorkout(
                selectedWorkoutEntity.value!!.id).map { exerciseEntity ->
                val sets = database.setDao().getSetsForExercise(selectedWorkoutEntity.value!!.id, exerciseEntity.id).map { setEntity ->
                    ExerciseSet(
                        weight = setEntity.weight,
                        unit = setEntity.unit,
                        reps = setEntity.reps
                    )
                }.toMutableList()

                Exercise(
                    id = exerciseEntity.id.toString(),
                    name = exerciseEntity.name,
                    bodyPart = exerciseEntity.bodyPart,
                    category = exerciseEntity.category,
                    image = exerciseEntity.image,
                    sets = sets
                )
            }
            exercises.addAll(
                exercisesForWorkout.map { exercise ->
                    exercise.copy(id = UUID.randomUUID().toString())
                }
            )
            isLoading.value = false
        }
    }
    
    if(showAlert.value){
        AlertDialog(
            modifier = Modifier,
            shape = RoundedCornerShape(16.dp),
            onDismissRequest = { showAlert.value = false },
            title = {
                Text(
                    text = "Are you sure you want to delete this workout?",
                    fontSize = 20.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch{
                            selectedWorkoutEntity.value?.let {
                                database.setDao().deleteSetsByWorkoutId(it.id)
                                database.workoutDao().deleteWorkoutExercisesById(it.id)
                                database.workoutDao().deleteWorkoutById(it.id)
                            }
                            onWorkoutDelete()
                            smthChanged.value = !smthChanged.value
                            if (workoutEntities.isEmpty()){
                                onDismissRequest()
                            }
                            showAlert.value = false
                        }},
                    content = { Text(text = "Yes", fontSize = 16.sp) }
                )
            },
            dismissButton = {
                TextButton(
                    onClick = { showAlert.value = false },
                    content = { Text(text = "No", fontSize = 16.sp) }
                )
            }
        )
    }

    ModalBottomSheet(
        modifier = Modifier
            .padding(top = 32.dp),
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
    ){
        if (workoutEntities.isNotEmpty()) {
            if (workoutEntities.size > 1 && !showWorkout.value){
                workoutEntities.forEach { workoutEntity ->
                    WorkoutItem(
                        workoutEntity = workoutEntity,
                        onClick = {
                            selectedWorkoutEntity.value = workoutEntity
                            showWorkout.value = true
                        }
                    )
                }
            }else if (selectedWorkoutEntity.value != null){
                val startTime = selectedWorkoutEntity.value!!.startTime
                val endTime = selectedWorkoutEntity.value!!.duration + startTime
                val spf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                spf.timeZone = TimeZone.getTimeZone("UTC")
                val startTimeFormatted = spf.format(Date(startTime))
                val endTimeFormatted = spf.format(Date(endTime))

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Text(
                        modifier = Modifier
                            .padding(bottom = 8.dp),
                        text = "$startTimeFormatted - $endTimeFormatted"
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        IconButton(
                            modifier = Modifier
                                .size(40.dp),
                            onClick = { showAlert.value = true }
                        ) {
                            Icon(
                                modifier = Modifier
                                    .size(32.dp),
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = "Delete workout"
                            )
                        }

                        Box(
                            modifier = Modifier
                                .width(120.dp)
                                .height(40.dp)
                                .clip(RoundedCornerShape(50))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = spf.format(Date(selectedWorkoutEntity.value!!.duration)),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }

                        IconButton(
                            modifier = Modifier
                                .size(40.dp),
                            onClick = {
                                val intent = Intent(context, WorkoutEditorActivity::class.java)
                                intent.putExtra("workoutId", selectedWorkoutEntity.value!!.id.toString())
                                intent.putExtra("unit", unit)
                                intent.putExtra("theme", theme)
                                intent.putExtra("dynamicColor", dynamicColor)
                                intent.putExtra("primaryColor", primaryColor)
                                intent.putExtra("screenOn", screenOn)
                                launcher.launch(intent)
                            }
                        ) {
                            Icon(
                                modifier = Modifier
                                    .size(32.dp),
                                imageVector = Icons.Outlined.Edit,
                                contentDescription = "Edit workout"
                            )
                        }

                    }
                    if(isLoading.value){
                        CircularProgressIndicator()
                    }else{
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            exercises.forEach { exercise ->
                                SelectedExerciseItemForCalendarSheet(
                                    exercise = exercise,
                                    unit = exercise.sets[0].unit,
                                    onExerciseChange = {}
                                )

                            }
                        }
                    }

                }
            }
        }
    }
}

@Preview
@Composable
fun CalendarSheetPreview(){
    Workout_App_2Theme {
        val workouts = listOf(Exercise(
            name = "Bench Press",
            bodyPart = "Chest",
            category = "Barbell",
            image = null,
            sets = mutableListOf(
                ExerciseSet(
                    weight = "135",
                    unit = "KG",
                    reps = "10"
                )
            )
        ))
        Surface(
            onClick = { /*TODO*/ }
        ) {
            if (workouts.isNotEmpty()) {
                if (workouts.size > 1){
                    Text(text = "Workouts")
                }else{
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                    ){
                        Text(text = "Workout")
                    }
                }
            }
        }

    }
}