package com.example.workout_app_2.presentation

import android.app.Activity
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.workout_app_2.data.DatabaseProvider
import com.example.workout_app_2.data.Exercise
import com.example.workout_app_2.data.ExerciseRepository
import com.example.workout_app_2.data.ExerciseSet
import com.example.workout_app_2.data.SetEntity
import com.example.workout_app_2.data.TemplateExerciseEntity
import com.example.workout_app_2.data.TemplateSetEntity
import com.example.workout_app_2.data.WorkoutExerciseEntity
import com.example.workout_app_2.ui.theme.Workout_App_2Theme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID

class WorkoutEditorActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        val workoutId = intent.getStringExtra("workoutId")
        val unit = intent.getStringExtra("unit")
        val theme = intent.getStringExtra("theme")
        val dynamicColor = intent.getBooleanExtra("dynamicColor", true)
        val primaryColor = intent.getIntExtra("primaryColor", 0xFF6650a4.toInt())
        val screenOn = intent.getBooleanExtra("screenOn", false)

        enableEdgeToEdge()
        setContent {
            val viewModel: WorkoutEditorViewModel = viewModel()
            WorkoutEditorScreen(workoutId, viewModel, unit, theme, dynamicColor, primaryColor, screenOn)
        }
    }

    private fun mergeExercises(exercises: List<Exercise>): List<Exercise> {
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

    @Composable
    fun WorkoutEditorScreen(
        workoutId: String?,
        viewModel: WorkoutEditorViewModel,
        unit : String?,
        theme: String?,
        dynamicColor: Boolean = true,
        primaryColor: Int = 0xFF6650a4.toInt(),
        screenOn: Boolean = false
    ){
        val context = LocalContext.current
        val database = DatabaseProvider.getDatabase(context)
        val scope = rememberCoroutineScope()

        var exercises by remember { mutableStateOf<List<Exercise>>(emptyList()) }
        val exercisesToShow = remember { mutableStateListOf<Exercise>() }

        val darkTheme: Boolean = when (theme) {
            "Dark" -> {
                true
            }
            "Light" -> {
                false
            }
            else -> {
                isSystemInDarkTheme()
            }
        }

        LaunchedEffect(Unit) {
            exercises = ExerciseRepository.getExercisesMapped(context)
            val exercisesForWorkout = database.workoutDao().getExercisesForWorkout(workoutId!!.toInt()).map { exerciseEntity ->
                val sets = database.setDao().getSetsForExercise(workoutId.toInt(), exerciseEntity.id).map { setEntity ->
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
            viewModel.selectedExercises.addAll(
                exercisesForWorkout.map { exercise ->
                    exercise.copy(id = UUID.randomUUID().toString())
                }
            )
        }

        var showAlert by remember { mutableStateOf(false) }
        var showDialog by rememberSaveable { mutableStateOf(false) }
        val snackbarHostState = remember { SnackbarHostState() }

        Workout_App_2Theme(darkTheme = darkTheme, dynamicColor = dynamicColor, customPrimaryColor = Color(primaryColor)){

            val activity = LocalContext.current as Activity
            LaunchedEffect(screenOn) {
                if (screenOn) {
                    activity.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                } else {
                    activity.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                }
            }

            if(showDialog){
                ExerciseSelector(
                    exercises = exercises,
                    onDismiss = {
                        showDialog = false
                    },
                    onExerciseSelected = { exercise ->
                        val exerciseWithId = exercise.copy(id = UUID.randomUUID().toString())
                        viewModel.selectedExercises.add(exerciseWithId)
                        showDialog = false
                    }
                )
            }

            if(showAlert){
                AlertDialog(
                    modifier = Modifier,
                    shape = RoundedCornerShape(16.dp),
                    onDismissRequest = { showAlert = false },
                    title = {
                        Text(
                            text = "Are you sure you want to save changes?",
                            fontSize = 20.sp
                        )
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                val mergedExercises = mergeExercises(viewModel.selectedExercises)
                                scope.launch{
                                    database.setDao().deleteSetsByWorkoutId(workoutId!!.toInt())
                                    database.workoutDao().deleteWorkoutExercisesById(workoutId.toInt())

                                    mergedExercises.forEachIndexed { index, exercise ->
                                        val exerciseId = database.exerciseDao().getExerciseIdByName(exercise.name)
                                        val workoutExercise = WorkoutExerciseEntity(
                                            workoutId = workoutId.toInt(),
                                            exerciseId = exerciseId,
                                            orderIndex = index
                                        )
                                        database.workoutDao().insertWorkoutExercise(workoutExercise)
                                        exercise.sets.forEach { set ->
                                            val workoutSetEntity = SetEntity(
                                                weight = set.weight,
                                                unit = set.unit!!,
                                                reps = set.reps,
                                                exerciseId = exerciseId,
                                                workoutId = workoutId.toInt()
                                            )
                                            database.setDao().insertSet(workoutSetEntity)
                                        }
                                    }
                                    showAlert = false
                                    viewModel.selectedExercises.clear()
                                    setResult(RESULT_OK)
                                    finish()
                                }
                            },
                            content = { Text(text = "Yes", fontSize = 16.sp) }
                        )
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { showAlert  = false },
                            content = { Text(text = "No", fontSize = 16.sp) }
                        )
                    }
                )
            }

            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ){
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 38.dp)
                                .padding(horizontal = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = {
                                    finish()
                                }
                            ) {
                                Icon(
                                    modifier = Modifier
                                        .size(24.dp),
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Close",
                                )
                            }

                            Text(
                                text = "Edit workout",
                                fontSize = MaterialTheme.typography.titleMedium.fontSize
                            )

                            Button(
                                modifier = Modifier
                                    .width(88.dp)
                                    .height(40.dp),
                                enabled = viewModel.selectedExercises.isNotEmpty(),
                                onClick = {
                                    showAlert = true
                                },

                                shape = RoundedCornerShape(18.dp)
                            ) {
                                Text(text = "SAVE")
                            }
                        }
                        SnackbarHost(
                            modifier = Modifier
                                .offset(y = 30.dp),
                            hostState = snackbarHostState)
                    },
                    floatingActionButton = {
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ){
                            FloatingActionButton(
                                onClick = {
                                    showDialog = true
                                },
                                shape = RoundedCornerShape(20.dp),
                                containerColor = MaterialTheme.colorScheme.primary,
                                elevation = FloatingActionButtonDefaults.elevation(0.dp)
                            ) {
                                Icon(Icons.Filled.Add, contentDescription = "Add Exercise")
                            }
                        }
                    }
                ) { paddingValues ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(paddingValues)
                            .padding(horizontal = 14.dp)
                            .verticalScroll(rememberScrollState())
                    ){
                        viewModel.selectedExercises.forEachIndexed{ index, exercise ->
                            key(exercise.id) {
                                SelectedExerciseItemForTemplateCreator(
                                    exercise = exercise,
                                    unit = unit,
                                    onDelete = {
                                        viewModel.selectedExercises.remove(exercise)
                                    },
                                    onExerciseChange = { updatedExercise ->
                                        if (index != -1) {
                                            viewModel.selectedExercises[index] = updatedExercise

                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
