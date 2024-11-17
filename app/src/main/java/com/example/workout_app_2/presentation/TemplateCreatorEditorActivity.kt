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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.workout_app_2.data.Exercise
import com.example.workout_app_2.data.ExerciseRepository
import com.example.workout_app_2.ui.theme.Workout_App_2Theme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.workout_app_2.data.DatabaseProvider
import com.example.workout_app_2.data.ExerciseSet
import com.example.workout_app_2.data.PreferencesDataStore
import com.example.workout_app_2.data.PreferencesViewModel
import com.example.workout_app_2.data.TemplateEntity
import com.example.workout_app_2.data.WorkoutTemplate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class TemplateCreatorEditorActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        val templateName = intent.getStringExtra("templateName")
        val unit = intent.getStringExtra("unit")
        val theme = intent.getStringExtra("theme")
        val dynamicColor = intent.getBooleanExtra("dynamicColor", true)
        val primaryColor = intent.getIntExtra("primaryColor", 0xFF6650a4.toInt())
        val screenOn = intent.getBooleanExtra("screenOn", false)

        enableEdgeToEdge()
        setContent {

            val viewModel: TemplateCreatorEditorViewModel = viewModel()
            TemplateCreatorEditorScreen(templateName, viewModel, unit, theme, dynamicColor, primaryColor, screenOn)

        }
    }

    @Composable
    fun TemplateCreatorEditorScreen(
        templateName: String? = null,
        viewModel: TemplateCreatorEditorViewModel,
        unit : String?,
        theme: String?,
        dynamicColor: Boolean = true,
        primaryColor: Int = 0xFF6650a4.toInt(),
        screenOn: Boolean = false
    ) {
        val context = LocalContext.current
        var exercises by remember { mutableStateOf<List<Exercise>>(emptyList()) }
        val scope = rememberCoroutineScope()
        var template: (TemplateEntity)?
        var workoutTemplate by remember { mutableStateOf<WorkoutTemplate?>(null) }
        var titleOfScreen by remember { mutableStateOf("Create Template") }

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

        val database = DatabaseProvider.getDatabase(context)
        LaunchedEffect(Unit) {
            exercises = ExerciseRepository.getExercisesMapped(context)
            if(templateName != null){
                titleOfScreen = "Edit $templateName"
                template = database.templateDao().getTemplateByName(templateName)
                workoutTemplate =
                    template?.let {
                        WorkoutTemplate(
                            name = it.name,
                            exercises = database.templateDao().getExercisesForTemplate(it.id).map { exerciseEntity ->
                                val sets = database.templateSetDao().getSetsForExercise(it.id, exerciseEntity.id).map { setEntity ->
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
                        )
                    }
                val newExercises = workoutTemplate?.exercises?.map { exercise ->
                    exercise.copy(id = UUID.randomUUID().toString())
                }
                if (newExercises != null && viewModel.selectedExercises.isEmpty()) {
                    viewModel.selectedExercises.addAll(newExercises)
                }
            }


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
                if(templateName != null){
                    TemplateCreatorDialog(
                        selectedExercises = viewModel.selectedExercises,
                        initialTemplate = workoutTemplate,
                        onDismiss = { showAlert = false },
                        onSuccess = {
                            scope.launch {
                                showAlert = false
                                snackbarHostState.showSnackbar("Template edited successfully")
                            }
                            scope.launch {
                                setResult(RESULT_OK)
                                delay(1000)
                                viewModel.selectedExercises.clear()
                                finish()
                            }
                        }
                    )
                }else{
                    TemplateCreatorDialog(
                        selectedExercises = viewModel.selectedExercises,
                        initialTemplate = null,
                        onDismiss = { showAlert = false },
                        onSuccess = {
                            scope.launch {
                                snackbarHostState.showSnackbar("Template created successfully")
                            }
                            viewModel.selectedExercises.clear()
                            setResult(RESULT_OK)
                            showAlert = false
                        }
                    )
                }

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
                                text = titleOfScreen,
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

    @Preview(
        showBackground = true,
        showSystemUi = true
    )
    @Composable
    fun PreviewTemplateCreatorEditorScreen() {
        Workout_App_2Theme {
            TemplateCreatorEditorScreen(templateName = null, viewModel = TemplateCreatorEditorViewModel(), unit = "KG", theme = "Default")
        }
    }
}