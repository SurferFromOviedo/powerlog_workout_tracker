package com.example.workout_app_2.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.workout_app_2.data.DatabaseProvider
import com.example.workout_app_2.data.ExerciseEntity
import com.example.workout_app_2.ui.theme.Workout_App_2Theme
import kotlinx.coroutines.launch
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TextButton
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.workout_app_2.data.Exercise
import com.example.workout_app_2.data.TemplateEntity
import com.example.workout_app_2.data.TemplateExerciseEntity
import com.example.workout_app_2.data.TemplateSetEntity
import com.example.workout_app_2.data.WorkoutTemplate

@Composable
fun TemplateCreatorDialog(
    selectedExercises: List<Exercise>,
    initialTemplate: WorkoutTemplate?,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit
){
    val template by remember { mutableStateOf(initialTemplate) }
    var templateEntity by remember { mutableStateOf<TemplateEntity?>(null) }
    var templateNames by remember { mutableStateOf<List<String>>(emptyList()) }
    val context = LocalContext.current
    val database = remember { DatabaseProvider.getDatabase(context) }
    val scope = rememberCoroutineScope()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(database) {
        templateNames = database.templateDao().getAllTemplateNames()
        if (template != null){
            templateEntity = database.templateDao().getTemplateByName(template!!.name)
        }

    }

    val title: String
    val buttonText: String

    var nameQuery by remember { mutableStateOf(template?.name ?: "") }

    if(template != null){
        title = "Edit template"
        buttonText = "Edit"
    }else{
        title = "Add template"
        buttonText = "Add"
    }

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

    Dialog(
        onDismissRequest = {onDismiss()},
        DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true, usePlatformDefaultWidth = false)
    ){
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ){
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(130.dp),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.background
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { onDismiss() },
                        ){
                            Icon(
                                modifier = Modifier
                                    .size(24.dp),
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                        Text(
                            text = title,
                            fontSize = 16.sp,
                        )
                    }

                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .clip(RoundedCornerShape(50.dp))
                    ){
                        BasicTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(35.dp),
                            value = nameQuery,
                            onValueChange = {nameQuery = it},
                            decorationBox = { innerTextField ->
                                Box(
                                    modifier = Modifier
                                        .background(
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                                            RoundedCornerShape(8.dp)
                                        )
                                        .padding(horizontal = 8.dp, vertical = 8.dp)
                                ) {
                                    innerTextField()
                                }
                            }
                        )

                        Row(modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp, horizontal = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween) {

                            if(nameQuery.isEmpty()){
                                Text(modifier = Modifier,
                                    text = "Template name",
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                                    fontSize = 14.sp
                                )
                            }else{
                                Text(modifier = Modifier,
                                    text = "               ",
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                                    fontSize = 14.sp
                                )
                            }

                        }
                    }

                    TextButton(
                        onClick = {
                            val mergedExercises = mergeExercises(selectedExercises)
                            if(template == null){
                                val trimmedNameQuery = nameQuery.trimEnd()
                                if(trimmedNameQuery.isNotEmpty()){
                                    if(trimmedNameQuery !in templateNames){
                                        scope.launch {
                                            //Write workout and info to database
                                            val newTemplate = TemplateEntity(
                                                name = trimmedNameQuery
                                            )
                                            val templateId = database.templateDao().insertTemplate(newTemplate)

                                            mergedExercises.forEachIndexed { index, exercise ->
                                                val exerciseId = database.exerciseDao().getExerciseIdByName(exercise.name)
                                                val templateExercise = TemplateExerciseEntity(
                                                    templateId = templateId.toInt(),
                                                    exerciseId = exerciseId,
                                                    orderIndex = index
                                                )
                                                database.templateDao().insertTemplateExercise(templateExercise)

                                                exercise.sets.forEach { set ->
                                                    val templateSetEntity = TemplateSetEntity(
                                                        weight = set.weight,
                                                        unit = set.unit!!,
                                                        reps = set.reps,
                                                        exerciseId = exerciseId,
                                                        templateId = templateId.toInt()
                                                    )
                                                    database.templateSetDao().insertTemplateSet(templateSetEntity)
                                                }
                                            }
                                            onSuccess()
                                        }

                                    }else{
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Template name already exists. Choose another name")
                                        }
                                    }
                                }else{
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Please fill the name")
                                    }
                                }
                            }else{
                                val trimmedNameQuery = nameQuery.trimEnd()
                                if(trimmedNameQuery.isNotEmpty()){
                                    if(trimmedNameQuery !in templateNames || trimmedNameQuery == template!!.name){
                                        scope.launch {
                                            templateEntity?.let { existingTemplate ->
                                                existingTemplate.name = trimmedNameQuery
                                                database.templateDao().insertTemplate(existingTemplate)

                                                database.templateDao().deleteTemplateExercisesByTemplateId(existingTemplate.id)
                                                database.templateSetDao().deleteTemplateSetsByTemplateId(existingTemplate.id)


                                                mergedExercises.forEachIndexed { index, exercise ->
                                                    val exerciseId = database.exerciseDao().getExerciseIdByName(exercise.name)
                                                    val templateExercise = templateEntity?.id?.let {
                                                        TemplateExerciseEntity(
                                                            templateId = existingTemplate.id,
                                                            exerciseId = exerciseId,
                                                            orderIndex = index
                                                        )
                                                    }
                                                    if (templateExercise != null) {
                                                        database.templateDao().insertTemplateExercise(templateExercise)
                                                    }

                                                    exercise.sets.forEach { set ->
                                                        val templateSetEntity = TemplateSetEntity(
                                                            weight = set.weight,
                                                            unit = set.unit!!,
                                                            reps = set.reps,
                                                            exerciseId = exerciseId,
                                                            templateId = existingTemplate.id
                                                        )
                                                        database.templateSetDao().insertTemplateSet(templateSetEntity)
                                                    }
                                                }
                                                onSuccess()
                                            }


                                        }
                                    }else{
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Template name already exists. Choose another name")
                                        }
                                    }
                                }else{
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Please fill the name")
                                    }
                                }

                            }
                        }
                    ){
                        Text(
                            text = buttonText,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.TopCenter
            ){
                SnackbarHost(hostState = snackbarHostState)
            }
        }
    }
}


@Preview(
    showBackground = true
)
@Composable
fun TemplateExerciseCreatorDialog(){
    val snackbarHostState = SnackbarHostState()
    Workout_App_2Theme {
        TemplateCreatorDialog(selectedExercises = emptyList(), initialTemplate = null, onDismiss = {}, onSuccess = {})
    }
}