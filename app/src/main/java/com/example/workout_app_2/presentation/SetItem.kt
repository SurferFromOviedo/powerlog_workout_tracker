package com.example.workout_app_2.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.workout_app_2.data.ExerciseSet
import com.example.workout_app_2.data.PreferencesDataStore
import com.example.workout_app_2.data.PreferencesViewModel
import com.example.workout_app_2.ui.theme.Workout_App_2Theme

@Composable
fun SetItem(
    set: ExerciseSet,
    preferencesViewModel: PreferencesViewModel,
    onSetChange: (ExerciseSet) -> Unit
){
    val unitStore by preferencesViewModel.unitFlow.collectAsState()


    var weight by remember { mutableStateOf(set.weight) }
    var unit by remember { mutableStateOf(set.unit ?: unitStore) }
    var reps by remember { mutableStateOf(set.reps) }

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(weight, unit, reps) {
        onSetChange(set.copy(weight = weight, unit = unit, reps = reps))
        if (reps.isEmpty() && weight.isEmpty()) {
            focusRequester.requestFocus()
        }
    }

    Surface(modifier = Modifier
        .padding(bottom = 0.dp)
        .height(45.dp)
        .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)

    ) {
        Row(modifier = Modifier
            .background(MaterialTheme.colorScheme.primary.copy(0.2f))
            .clip(RoundedCornerShape(40))
            .padding(horizontal = 8.dp)
            .padding(bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
            ){
            BasicTextField(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
                    .height(35.dp)
                    .clip(RoundedCornerShape(40))
                    .background(MaterialTheme.colorScheme.primary.copy(0.4f))
                    .focusRequester(focusRequester),
                value = weight,
                onValueChange = { newValue ->
                    if(newValue.all {it.isDigit() || it == '.'}){
                        var updatedValue = newValue
                        if(updatedValue.length == 1){
                            if(updatedValue[0] == '.'){
                                updatedValue = newValue.drop(1)
                            }
                        }
                        if(updatedValue.length >= 2){
                            if(updatedValue[0] == '0' && updatedValue[1] != '.'){
                                updatedValue = newValue.drop(1)
                            }
                        }

                        val dotIndex = updatedValue.indexOf('.')
                        if (dotIndex != -1 && updatedValue.count { it == '.' } > 1) {
                            updatedValue = updatedValue.removeRange(dotIndex + 1, updatedValue.length).plus(updatedValue.substring(dotIndex + 1).filter { it != '.' })
                        }

                        weight = updatedValue
                    }
                },
                textStyle = TextStyle(textAlign = TextAlign.Center, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        innerTextField()
                    }
                    Box(modifier = Modifier
                        .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        if (weight.isEmpty()) {
                            Text(
                                text = "Weight",
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            )

            Button(modifier = Modifier
                .size(35.dp),
                onClick = {
                    unit = if (unit == "KG") "LB" else "KG"
                          },
                shape = RoundedCornerShape(40),
                colors =  ButtonDefaults.buttonColors(containerColor = colorScheme.primary.copy(0.3f)),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(modifier = Modifier,
                    text = unit,
                    fontSize = 12.sp,
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    overflow = TextOverflow.Ellipsis
                )
            }

            BasicTextField(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
                    .height(35.dp)
                    .clip(RoundedCornerShape(40))
                    .background(MaterialTheme.colorScheme.primary.copy(0.4f)),
                value = reps,
                onValueChange = { newValue ->
                    if(newValue.all {it.isDigit()}){
                        var updatedValue = newValue
                        if(newValue.length >= 2){
                            if(newValue[0] == '0'){
                                updatedValue = newValue.drop(1)
                            }
                        }
                        if(updatedValue.length > 9){
                            updatedValue = updatedValue.take(9)
                        }
                        reps = updatedValue
                    }
                },
                textStyle = TextStyle(textAlign = TextAlign.Center, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                        ) {

                        innerTextField()
                    }

                    Box(modifier = Modifier
                        .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        if (reps.isEmpty()) {
                            Text(
                                fontSize = 15.sp,
                                text = "Reps",
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            )

        }
    }
}


@Preview
@Composable
fun PreviewSetItem(){
    val fakeSet = ExerciseSet("100", "KG", "10")
    Workout_App_2Theme {
        SetItem(fakeSet,PreferencesViewModel(PreferencesDataStore(LocalContext.current)), onSetChange = {})
    }
}