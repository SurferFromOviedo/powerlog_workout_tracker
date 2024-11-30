package com.example.workout_app_2.presentation

import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.workout_app_2.data.DatabaseProvider
import com.example.workout_app_2.data.Exercise
import com.example.workout_app_2.data.ExerciseRepository
import com.example.workout_app_2.data.ExerciseSet
import com.example.workout_app_2.data.PreferencesDataStore
import com.example.workout_app_2.data.PreferencesViewModel
import com.example.workout_app_2.data.SetEntity
import com.example.workout_app_2.data.WorkoutEntity
import com.example.workout_app_2.data.WorkoutExerciseEntity
import com.example.workout_app_2.data.WorkoutTemplate
import com.example.workout_app_2.ui.theme.Workout_App_2Theme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import java.util.UUID

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WorkoutScreen(
    preferencesViewModel: PreferencesViewModel,
    showDialog: Boolean,
    onShowDialogChange: (Boolean) -> Unit,
    onSelectedExerciseChange: (Boolean) -> Unit
){
    //Get data from preferences data store
    val isRunningStore by preferencesViewModel.isRunningFlow.collectAsState(initial = false)
    val startTimeStore by preferencesViewModel.startTimeFlow.collectAsState(initial = 0L)
    val accumulatedTimeStore by preferencesViewModel.accumulatedTimeFlow.collectAsState(initial = 0L)
    val savedExercises by preferencesViewModel.exerciseFlow.collectAsState(initial = emptyList())

    val snackbarHostState = remember { SnackbarHostState() }

    //Set data to variables
    var isRunning by remember { mutableStateOf(isRunningStore) }
    var startTime by remember { mutableLongStateOf(startTimeStore) }
    var accumulatedTime by remember { mutableLongStateOf(accumulatedTimeStore) }
    //Time in Box
    var currentTime by remember { mutableLongStateOf(0L) }

    //Scope for coroutines
    val scope = rememberCoroutineScope()

    //Exercises selected from ExerciseTemplateSelector
    //Are shown in Column in WorkoutScreen
    val selectedExercises = remember(savedExercises) {
        mutableStateListOf<Exercise>().apply { addAll(savedExercises) }
    }

    //Update workoutState in MainActivity
    LaunchedEffect(selectedExercises) {
        onSelectedExerciseChange(selectedExercises.isNotEmpty())
    }

    //Database initialization
    val context = LocalContext.current
    val database = DatabaseProvider.getDatabase(context)

    //Get all exercises from database
    //Will be given to ExerciseTemplateSelector
    var exercises by remember { mutableStateOf<List<Exercise>>(emptyList()) }

    //For workout templates
    var workoutTemplates by remember { mutableStateOf<List<WorkoutTemplate>>(emptyList()) }

    LaunchedEffect(Unit) {
        //Get all exercises from database
        exercises = ExerciseRepository.getExercisesMapped(context)
        //Get all templates from database
        val templateEntities = database.templateDao().getAllTemplates()
        //Map template entities to workout templates
        workoutTemplates = templateEntities.map { templateEntity ->
            WorkoutTemplate(
                name = templateEntity.name,
                exercises = withContext(Dispatchers.IO) {
                    database.templateDao().getExercisesForTemplate(templateEntity.id).map { exerciseEntity ->
                        val sets = database.templateSetDao().getSetsForExercise(
                            templateEntity.id,
                            exerciseEntity.id
                        ).map { templateSetEntity ->
                            ExerciseSet(
                                weight = templateSetEntity.weight,
                                unit = templateSetEntity.unit,
                                reps = templateSetEntity.reps
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
                }
            )
        }
    }


    //Flag of whether to show alert dialog
    var showAlert by rememberSaveable { mutableStateOf(false) }

    //Timer functions
    fun startTimer() {
        if(!isRunning){
            val currentSystemTime = System.currentTimeMillis()
            isRunning = true
            startTime = currentSystemTime
            scope.launch {
                while (isRunning) {
                    currentTime = (System.currentTimeMillis() - startTime) + accumulatedTime
                    delay(1000L)
                    preferencesViewModel.saveTimer(startTime, accumulatedTime, isRunning)
                }
            }
        }
    }

    fun pauseTimer() {
        if (isRunning) {
            isRunning = false
            accumulatedTime += System.currentTimeMillis() - startTime
            scope.launch {
                preferencesViewModel.saveTimer(startTime, accumulatedTime, isRunning)
            }
        }
    }

    fun resetTimer() {
        isRunning = false
        startTime = 0L
        accumulatedTime = 0L
        currentTime = 0L
        scope.launch {
            preferencesViewModel.saveTimer(0L, 0L, isRunning)
        }
    }

    //Merge exercises with same name to avoid duplicates, merge sets
    fun mergeExercises(exercises: List<Exercise>): List<Exercise> {
        val mergedExercises = mutableMapOf<String, Exercise>()
        exercises.forEach { exercise ->
            val existingExercise = mergedExercises[exercise.name]
            if (existingExercise != null) {
                existingExercise.sets.addAll(exercise.sets)
            } else {
                mergedExercises[exercise.name] = exercise.copy()
            }
        }
        return mergedExercises.values.toList()
    }

    //Get data from preferences data store
    LaunchedEffect(isRunningStore, startTimeStore, accumulatedTimeStore) {
        isRunning = isRunningStore
        startTime = startTimeStore
        accumulatedTime = accumulatedTimeStore

        if (isRunning) {
            val currentSystemTime = System.currentTimeMillis()
            currentTime = (currentSystemTime - startTime) + accumulatedTime
            scope.launch {
                while (isRunning) {
                    currentTime = (System.currentTimeMillis() - startTime) + accumulatedTime
                    delay(500L)
                }
            }
        } else {
            currentTime = accumulatedTime
        }
    }

    //UI
    SnackbarHost(hostState = snackbarHostState, modifier = Modifier.padding(top = 48.dp))
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp)
            .padding(top = 14.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier
                    .width(88.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(onClick = {
                    if (isRunning) {
                        pauseTimer()
                    } else {
                        startTimer()
                    } },
                    modifier = Modifier
                        .size(40.dp),
                    shape = RoundedCornerShape(18.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    if (isRunning) {
                        Icon(imageVector = Icons.Filled.Stop, contentDescription = "Stop Time")
                    } else {
                        Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = "Start Time")
                    }
                }

                Button(
                    onClick = {
                        resetTimer() },
                    modifier = Modifier
                        .size(40.dp),
                        shape = RoundedCornerShape(18.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(imageVector = Icons.Filled.RestartAlt, contentDescription = "Reset Time")
                    }
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
                        text = String.format(
                            Locale.getDefault(),
                            "%02d:%02d:%02d",
                            currentTime / 3600000,
                            (currentTime % 3600000) / 60000,
                            (currentTime % 60000) / 1000
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                //Alert dialog will appear when user clicks end workout button
                if (showAlert) {
                    AlertDialog(
                        modifier = Modifier,
                        shape = RoundedCornerShape(16.dp),
                        onDismissRequest = { showAlert = false },
                        title = {
                            Text(
                                text = "Are you sure you want to end the workout?",
                                fontSize = 20.sp
                            )
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    val mergedExercises = mergeExercises(selectedExercises).toMutableStateList()
                                    if(startTime == 0L){
                                        startTime = System.currentTimeMillis()
                                    }
                                    val instant = Instant.ofEpochMilli(startTime)
                                    val localZoneId = ZoneId.systemDefault()
                                    val localDateTime = LocalDateTime.ofInstant(instant, localZoneId)
                                    val utcDateTime = localDateTime.atZone(localZoneId).withZoneSameLocal(
                                        ZoneOffset.UTC)
                                    val utcMillis = utcDateTime.toInstant().toEpochMilli()

                                    scope.launch {
                                        //Update exercise usage
                                        mergedExercises.forEach{ exercise ->
                                            val id = database.exerciseDao().getExerciseIdByName(exercise.name)
                                            val entity = database.exerciseDao().getExerciseById(id)
                                            val updatedUsageEntity = entity?.copy(usage = entity.usage + 1)
                                            if (updatedUsageEntity != null) {
                                                database.exerciseDao().insertExercise(updatedUsageEntity)
                                            }
                                        }

                                        //Write workout and info to database
                                        val workout = WorkoutEntity(
                                            startTime = utcMillis,
                                            duration = currentTime
                                        )
                                        val workoutId = database.workoutDao().insertWorkout(workout)

                                        mergedExercises.forEachIndexed { index, exercise ->
                                            val exerciseId = database.exerciseDao().getExerciseIdByName(exercise.name)
                                            val workoutExercise = WorkoutExerciseEntity(
                                                workoutId = workoutId.toInt(),
                                                exerciseId = exerciseId,
                                                orderIndex = index
                                            )
                                            database.workoutDao().insertWorkoutExercise(workoutExercise)

                                            exercise.sets.forEach { set ->
                                                val setEntity = SetEntity(
                                                    weight = set.weight,
                                                    unit = set.unit!!,
                                                    reps = set.reps,
                                                    exerciseId = exerciseId,
                                                    workoutId = workoutId.toInt()
                                                )
                                                database.setDao().insertSet(setEntity)
                                            }
                                        }

                                        //Clear workout screen
                                        selectedExercises.clear()
                                        preferencesViewModel.saveExercises(selectedExercises)
                                        resetTimer()
                                        showAlert = false

                                        //Create a new list to sort exercises by usage in ExerciseTemplateSelector
                                        exercises = ExerciseRepository.getExercisesMapped(context)
                                        snackbarHostState.showSnackbar("Workout saved successfully")
                                    }
                                },
                                content = { Text(text = "Yes", fontSize = 16.sp) }
                            )
                        },
                        dismissButton = {
                            TextButton(
                                onClick = { showAlert = false },
                                content = { Text(text = "No", fontSize = 16.sp) }
                            )
                        }
                    )
                }

                Button(
                    enabled = selectedExercises.isNotEmpty(),
                    onClick = {
                        showAlert = true
                    },
                    modifier = Modifier
                        .width(88.dp)
                        .height(40.dp),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Text(text = "END")
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                selectedExercises.forEachIndexed{ index, exercise ->
                    key(exercise.id) {
                        SelectedExerciseItem(
                            exercise = exercise,
                            preferencesViewModel = preferencesViewModel,
                            onDelete = {
                                selectedExercises.remove(exercise)
                                if (selectedExercises.isEmpty() && isRunning) {
                                    resetTimer()
                                }
                                scope.launch {
                                    preferencesViewModel.saveExercises(selectedExercises)
                                }
                            },
                            onExerciseChange = { updatedExercise ->
                                if (index != -1) {
                                    selectedExercises[index] = updatedExercise
                                    scope.launch {
                                        preferencesViewModel.saveExercises(selectedExercises)
                                    }
                                }
                            }
                        )
                    }

                }
            }

            //Show ExerciseTemplateSelector dialog
            if (showDialog) {
                ExerciseTemplateSelector(
                    exercises = exercises,
                    templates = workoutTemplates,
                    onDismiss = { onShowDialogChange(false) },
                    onExerciseSelected = { exercise ->
                        val exerciseWithId = exercise.copy(id = UUID.randomUUID().toString())
                        selectedExercises.add(exerciseWithId)
                        startTimer()
                        onShowDialogChange(false)
                    },
                    onTemplateSelected = { template ->
                        val newExercises = template.exercises.map { exercise ->
                            exercise.copy(id = UUID.randomUUID().toString())
                        }
                        selectedExercises.addAll(newExercises)
                        startTimer()
                        onShowDialogChange(false)
                    }
                )
            }
        }
    }


@RequiresApi(Build.VERSION_CODES.O)
@Preview(
    showBackground = true,
    showSystemUi = true)
@Composable
fun PreviewWorkoutScreen() {
    Workout_App_2Theme {
       // WorkoutScreen(PreferencesViewModel(PreferencesDataStore(LocalContext.current)), onShowDialogChange = {}, showDialog = false,onSelectedExerciseChange = {})
    }
}