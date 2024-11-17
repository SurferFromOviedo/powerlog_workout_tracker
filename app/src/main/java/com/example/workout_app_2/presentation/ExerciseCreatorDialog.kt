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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.workout_app_2.data.Exercise

@Composable
fun ExerciseCreatorDialog(
    exercise: Exercise?,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit
){
    var exercises by remember { mutableStateOf<List<ExerciseEntity>>(emptyList()) }
    var exerciseId by rememberSaveable { mutableStateOf<Int?>(null) }
    var exerciseEntity by remember { mutableStateOf<ExerciseEntity?>(null) }

    val context = LocalContext.current
    val database = remember { DatabaseProvider.getDatabase(context) }
    val scope = rememberCoroutineScope()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(database) {
        exercises = database.exerciseDao().getAllExercises()
        if (exercise != null) {
            exerciseId = database.exerciseDao().getExerciseIdByName(exercise.name)
            exerciseEntity = database.exerciseDao().getExerciseById(exerciseId!!)
        }
    }

    val exerciseNames = exercises.map { it.name }

    val name: String
    val bodyPart: String
    val category: String
    val title: String
    val buttonText: String


    if (exercise == null) {
        name = ""
        bodyPart = "Whole Body"
        category = "Barbell"
        title = "Add exercise"
        buttonText = "Add"
    } else {
        name = exercise.name
        bodyPart = exercise.bodyPart
        category = exercise.category
        title = "Edit exercise"
        buttonText = "Edit"
    }

    var nameQuery by rememberSaveable { mutableStateOf(name) }
    var selectedBodyPart by rememberSaveable { mutableStateOf(bodyPart) }
    var selectedCategory by rememberSaveable { mutableStateOf(category) }


    val bodyParts = listOf(
        "Whole Body",
        "Legs",
        "Back",
        "Chest",
        "Shoulders",
        "Biceps",
        "Triceps",
        "Core",
        "Forearms"
    )
    val categories = listOf(
        "Barbell",
        "Bodyweight",
        "Dumbbell",
        "Machine",
        "Cable"
    )

    Dialog(
        onDismissRequest = {onDismiss()},
        DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true, usePlatformDefaultWidth = false)
    ){
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(172.dp),
            contentAlignment = Alignment.Center
        ){
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(172.dp),
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
                                    text = "Exercise name",
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

                    Row(
                        modifier = Modifier
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)

                    ){
                        Spinner(
                            modifier = Modifier.weight(1f),
                            items = bodyParts, selectedItem = selectedBodyPart, onItemSelected = {item -> selectedBodyPart = item} )
                        Spinner(
                            modifier = Modifier.weight(1f),
                            items = categories, selectedItem = selectedCategory, onItemSelected =  {item -> selectedCategory = item})
                    }

                    TextButton(
                       onClick = {
                           if(exercise == null){
                               val trimmedNameQuery = nameQuery.trimEnd()
                               if(trimmedNameQuery.isNotEmpty()){
                                    if (trimmedNameQuery !in exerciseNames) {
                                        val newExercise = ExerciseEntity(
                                            name = trimmedNameQuery,
                                            bodyPart = selectedBodyPart,
                                            category = selectedCategory
                                        )
                                        scope.launch {
                                            database.exerciseDao().insertExercise(newExercise)
                                        }
                                        onDismiss()
                                        onSuccess()

                                    }else{
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Exercise with same name already exists")
                                        }

                                    }
                               }else{
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Please fill the name")
                                    }
                               }
                           }else{
                               val trimmedNameQuery = nameQuery.trimEnd()
                               if (trimmedNameQuery.isNotEmpty()){
                                   if (trimmedNameQuery !in exerciseNames || trimmedNameQuery == exercise.name){
                                       val editedExercise = exerciseEntity?.let {
                                           ExerciseEntity(
                                               name = trimmedNameQuery,
                                               bodyPart = selectedBodyPart,
                                               category = selectedCategory,
                                               id = exerciseId!!,
                                               usage = it.usage
                                           )
                                       }
                                       scope.launch {
                                           if (editedExercise != null) {
                                               database.exerciseDao().insertExercise(editedExercise)
                                           }
                                           onDismiss()
                                           onSuccess()
                                       }
                                   }else{
                                       scope.launch {
                                           snackbarHostState.showSnackbar("Exercise with same name already exists")
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
    showBackground = true,
    showSystemUi = true
)
@Composable
fun PreviewExerciseCreatorDialog(){
    val fakeExercise = Exercise("Hello", "Core", "Dumbbell", image = null)
    Workout_App_2Theme {
        ExerciseCreatorDialog(onDismiss = {}, onSuccess = {}, exercise = fakeExercise)
    }
}