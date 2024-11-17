package com.example.workout_app_2.presentation

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.workout_app_2.data.DatabaseProvider
import com.example.workout_app_2.data.Exercise
import com.example.workout_app_2.data.ExerciseRepository
import com.example.workout_app_2.data.ExerciseSet
import com.example.workout_app_2.data.PreferencesDataStore
import com.example.workout_app_2.data.PreferencesViewModel
import com.example.workout_app_2.data.TemplateEntity
import com.example.workout_app_2.data.WorkoutTemplate
import com.example.workout_app_2.ui.theme.Workout_App_2Theme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun SettingsScreen(
    workoutInProcess: Boolean,
    preferencesViewModel: PreferencesViewModel
) {
    val unitStore by preferencesViewModel.unitFlow.collectAsState()
    val themeStore by preferencesViewModel.themeFlow.collectAsState()
    val primaryColorStore by preferencesViewModel.primaryColorFlow.collectAsState()
    val dynamicColorStore by preferencesViewModel.dynamicColorFlow.collectAsState()
    val screenOnStore by preferencesViewModel.screenOnFlow.collectAsState()

    var theme by remember {mutableStateOf(themeStore) }
    var unit by remember {mutableStateOf(unitStore) }
    var primaryColor by remember { mutableIntStateOf(primaryColorStore) }
    var dynamicColor by remember { mutableStateOf(dynamicColorStore) }
    var screenOn by remember { mutableStateOf(screenOnStore) }

    val showDialog = remember { mutableStateOf(false) }
    val showSelector = remember { mutableStateOf(false) }
    val showTemplateSelector = remember { mutableStateOf(false) }
    val showAlert = remember { mutableStateOf(false) }
    val showResetAlert = remember { mutableStateOf(false) }
    val showAlertTemplate = remember { mutableStateOf(false) }
    var showColorPicker by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    var edit by remember { mutableStateOf(false) }

    var exercises by remember {mutableStateOf<List<Exercise>>(emptyList())}
    var workoutTemplates by remember { mutableStateOf<List<WorkoutTemplate>>(emptyList()) }

    val context = LocalContext.current
    val database = remember { DatabaseProvider.getDatabase(context) }
    val scope = rememberCoroutineScope()

    suspend fun mapTemplates(templateEntities: List<TemplateEntity>): List<WorkoutTemplate> {
        val mappedTemplates =
        templateEntities.map { templateEntity ->
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
        return mappedTemplates
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ){ result ->
        if(result.resultCode == Activity.RESULT_OK) {
            scope.launch {
                val templateEntities = database.templateDao().getAllTemplates()
                workoutTemplates = mapTemplates(templateEntities)
            }
        }
    }

    LaunchedEffect(database) {
        exercises = ExerciseRepository.getExercisesMapped(context)
        val templateEntities = database.templateDao().getAllTemplates()
        workoutTemplates = mapTemplates(templateEntities)
    }

    val areButtonsEnabled = rememberSaveable { mutableStateOf(!workoutInProcess) }
    LaunchedEffect(workoutInProcess) {
        areButtonsEnabled.value = !workoutInProcess
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        Text(text = "Exercises", fontWeight = FontWeight.Bold)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .padding(bottom = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ){
            Button(
                modifier = Modifier
                    .weight(1f),
                shape = RoundedCornerShape(40),
                onClick = {
                    showDialog.value = true
                }
            ) {
                Text(text = "Add")
            }
            Button(
                modifier = Modifier
                    .weight(1f),
                shape = RoundedCornerShape(40),
                onClick = {
                    edit = true
                    showSelector.value = true
                },
                enabled = areButtonsEnabled.value
            ) {
                Text(text = "Edit")
            }
            Button(
                modifier = Modifier
                    .weight(1f),
                shape = RoundedCornerShape(40),
                onClick = {
                    edit = false
                    showSelector.value = true
                },
                enabled = areButtonsEnabled.value
            ) {
               Text(text = "Delete")
            }

        }

        Row(
            modifier = Modifier
                .padding(bottom = 8.dp)
        ){
            Button(
                modifier = Modifier
                    .weight(1f),
                shape = RoundedCornerShape(40),
                onClick = {
                    showResetAlert.value = true
                }
            ){
                Text(text = "Clear usage data")
            }
        }
        HorizontalDivider()
        Spacer(modifier = Modifier.padding(4.dp))
        Text(text = "Templates", fontWeight = FontWeight.Bold)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ){
            Button(
                modifier = Modifier
                    .weight(1f),
                shape = RoundedCornerShape(40),
                onClick = {
                    val intent = Intent(context, TemplateCreatorEditorActivity::class.java)
                    intent.putExtra("unit", unit)
                    intent.putExtra("theme", theme)
                    intent.putExtra("dynamicColor", dynamicColor)
                    intent.putExtra("primaryColor", primaryColor)
                    intent.putExtra("screenOn", screenOn)
                    launcher.launch(intent)
                }
            ) {
                Text(text = "Add")
            }
            Button(
                modifier = Modifier
                    .weight(1f),
                shape = RoundedCornerShape(40),
                onClick = {
                    edit = true
                    showTemplateSelector.value = true
                }
            ) {
                Text(text = "Edit")
            }
            Button(
                modifier = Modifier
                    .weight(1f),
                shape = RoundedCornerShape(40),
                onClick = {
                    edit = false
                    showTemplateSelector.value = true
                }
            ) {
                Text(text = "Delete")
            }
            
        }

        HorizontalDivider()
        Spacer(modifier = Modifier.padding(4.dp))
        Text(text = "Units", fontWeight = FontWeight.Bold)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ){
            Row(
                modifier = Modifier
                    .weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ){
                RadioButton(
                    selected = unit == "KG",
                    onClick = {
                        unit = "KG"
                        preferencesViewModel.saveUnit(unit)
                    }
                )
                Text(
                    modifier = Modifier
                        .padding(end = 12.dp),
                    text = "KG"
                )
            }

            Row(modifier = Modifier
                .weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ){
                RadioButton(
                    selected = unit != "KG",
                    onClick = {
                        unit = "LB"
                        preferencesViewModel.saveUnit(unit)
                    }
                )
                Text(
                    modifier = Modifier
                        .padding(end = 12.dp),
                    text = "LB"
                )
            }
        }
        HorizontalDivider()
        Spacer(modifier = Modifier.padding(4.dp))
        Text(text = "Theme", fontWeight = FontWeight.Bold)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ){

            Row(
                modifier = Modifier
                    .weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ){
                RadioButton(
                    selected = theme == "Light",
                    onClick = {
                        theme = "Light"
                        preferencesViewModel.saveTheme(theme)
                    }
                )
                Text(
                    modifier = Modifier
                        .padding(end = 12.dp),
                    text = "Light"
                )
            }
            Row(
                modifier = Modifier
                    .weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ){
                RadioButton(
                    selected = theme == "Dark",
                    onClick = {
                        theme = "Dark"
                        preferencesViewModel.saveTheme(theme)

                    }
                )
                Text(
                    modifier = Modifier
                        .padding(end = 12.dp),
                    text = "Dark"
                )
            }
            Row(
                modifier = Modifier
                    .weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
            ){
                RadioButton(
                    selected = theme == "Default",
                    onClick = {
                        theme = "Default"
                        preferencesViewModel.saveTheme(theme)

                    }
                )
                Text(
                    text = "Default",
                    maxLines = 1
                )
            }
        }

        HorizontalDivider()
        Spacer(modifier = Modifier.padding(4.dp))
        Text(text = "Color scheme", fontWeight = FontWeight.Bold)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ){
            Row(
                modifier = Modifier
                    .padding(end = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ){
                Text(text = "Dynamic color:")
                Switch(
                    checked = dynamicColor,
                    onCheckedChange = {
                        dynamicColor = it
                        preferencesViewModel.saveDynamicColor(dynamicColor)
                    },
                    colors = SwitchDefaults.colors(
                        uncheckedTrackColor = MaterialTheme.colorScheme.surface
                    )
                )
            }

            Row(
                modifier = Modifier
                    .padding(start = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ){
                Button(
                    modifier = Modifier
                        .weight(1f),
                    shape = RoundedCornerShape(40),
                    onClick = {
                        showColorPicker = true
                    },
                    enabled = !dynamicColor
                ){
                    Text(text = "Choose color")
                }
            }
        }

        HorizontalDivider()
        Spacer(modifier = Modifier.padding(4.dp))
        Text(text = "Screen", fontWeight = FontWeight.Bold)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(
                modifier = Modifier
                    .padding(end = 4.dp),
                text = "Keep screen always on:"
            )
            Switch(
                modifier = Modifier
                    .padding(start = 4.dp),
                checked = screenOn,
                onCheckedChange = {
                    screenOn = it
                    preferencesViewModel.saveScreenOn(screenOn)
                },
                colors = SwitchDefaults.colors(
                    uncheckedTrackColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    }

    SnackbarHost(hostState = snackbarHostState)

    if(showColorPicker){
        ColorPickerDialog(initialColor = Color(primaryColor), onDismiss = { showColorPicker = false }, onColorSelected = {
            preferencesViewModel.savePrimaryColor(it)
            primaryColor = it.toArgb()
        })
    }


    var exercise by remember { mutableStateOf<Exercise?>(null) }

    if (showDialog.value) {
        if(!showSelector.value){
            ExerciseCreatorDialog(
                exercise = null,
                onDismiss = { showDialog.value = false },
                onSuccess = {
                    scope.launch {
                        snackbarHostState.showSnackbar("Exercise added successfully")
                        exercises = ExerciseRepository.getExercisesMapped(context)
                    }
                }
            )
        }else{
            ExerciseCreatorDialog(
                exercise = exercise,
                onDismiss = { showDialog.value = false },
                onSuccess = {
                    scope.launch {
                        exercises = ExerciseRepository.getExercisesMapped(context)
                        showSelector.value = false
                        showSelector.value = true
                        snackbarHostState.showSnackbar("Exercise edited successfully")
                    }
                }
            )
        }
    }

    if (showSelector.value){
        ExerciseSelector(
            exercises = exercises,
            onDismiss = {showSelector.value = false},
            onExerciseSelected = {
                if(!workoutInProcess){
                    if(edit){
                        showDialog.value = true
                    }else{
                        showAlert.value = true
                    }
                    exercise = it
                }else{
                    scope.launch {
                        snackbarHostState.showSnackbar("Cannot edit or delete exercises while workout is in progress")
                    }
                }
            }
        )
    }

    if(showResetAlert.value){
        AlertDialog(
            modifier = Modifier,
            shape = RoundedCornerShape(16.dp),
            onDismissRequest = { showResetAlert.value = false },
            title = {
                Text(
                    text = "Are you sure you want to clear usage data?",
                    fontSize = 20.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            database.exerciseDao().clearUsageData()
                            exercises = ExerciseRepository.getExercisesMapped(context)
                            showResetAlert.value = false
                            snackbarHostState.showSnackbar("Usage data cleared successfully")
                        }
                    },
                    content = { Text(text = "Yes", fontSize = 16.sp) }
                )
            },
            dismissButton = {
                TextButton(
                    onClick = { showResetAlert.value = false },
                    content = { Text(text = "No", fontSize = 16.sp) }
                )
            }
        )
    }

    if(showAlert.value){
        AlertDialog(
            modifier = Modifier,
            shape = RoundedCornerShape(16.dp),
            onDismissRequest = { showAlert.value = false },
            title = {
                Text(
                    text = "Are you sure you want to delete the ${exercise?.name}?",
                    fontSize = 20.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            val templateCount = database.templateDao().getTemplateCountByExerciseName(exercise!!.name)
                            val workoutCount = database.workoutDao().getWorkoutCountByExerciseName(exercise!!.name)
                            if (templateCount > 0 || workoutCount > 0) {
                                snackbarHostState.showSnackbar("Cannot delete exercise that is saved in a template or in a workout")
                            }else{
                                val exerciseId = database.exerciseDao().getExerciseIdByName(exercise!!.name)
                                val exerciseToDelete = database.exerciseDao().getExerciseById(exerciseId)
                                database.exerciseDao().deleteExercise(exerciseToDelete!!)
                                exercises = ExerciseRepository.getExercisesMapped(context)
                                showAlert.value = false
                                snackbarHostState.showSnackbar("${exercise?.name} deleted successfully")

                            }

                        }
                    },
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

    var template by remember { mutableStateOf<WorkoutTemplate?>(null) }

    if(showAlertTemplate.value){
        AlertDialog(
            modifier = Modifier,
            shape = RoundedCornerShape(16.dp),
            onDismissRequest = { showAlertTemplate.value = false },
            title = {
                Text(
                    text = "Are you sure you want to delete the ${template?.name}?",
                    fontSize = 20.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            val templateEntity = database.templateDao().getTemplateByName(template!!.name)
                            database.templateSetDao().deleteTemplateSetsByTemplateId(templateEntity!!.id)
                            database.templateDao().deleteTemplateExercisesByTemplateId(templateEntity.id)
                            database.templateDao().deleteTemplateByTemplateId(templateEntity.id)
                            workoutTemplates = mapTemplates(database.templateDao().getAllTemplates())
                            showAlertTemplate.value = false
                            snackbarHostState.showSnackbar("${template?.name} deleted successfully")
                        }
                    },
                    content = { Text(text = "Yes", fontSize = 16.sp) }
                )
            },
            dismissButton = {
                TextButton(
                    onClick = { showAlertTemplate.value = false },
                    content = { Text(text = "No", fontSize = 16.sp) }
                )
            }
        )

    }

    if(showTemplateSelector.value){
        TemplateSelector(
            templates = workoutTemplates,
            onDismiss = { showTemplateSelector.value = false },
            onTemplateSelected = {
                if(edit){
                    showTemplateSelector.value = false
                    val intent = Intent(context, TemplateCreatorEditorActivity::class.java)
                    intent.putExtra("templateName", it.name)
                    intent.putExtra("unit", unit)
                    intent.putExtra("theme", theme)
                    intent.putExtra("dynamicColor", dynamicColor)
                    intent.putExtra("primaryColor", primaryColor)
                    intent.putExtra("screenOn", screenOn)
                    launcher.launch(intent)
                }else{
                    showAlertTemplate.value = true
                }
                template = it
            }
        )
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun PreviewSettingsScreen() {
    Workout_App_2Theme {
        SettingsScreen(workoutInProcess = false, PreferencesViewModel(PreferencesDataStore(LocalContext.current)))
    }
}