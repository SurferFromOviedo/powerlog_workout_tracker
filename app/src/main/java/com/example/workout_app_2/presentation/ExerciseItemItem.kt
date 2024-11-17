package com.example.workout_app_2.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow.Companion.Ellipsis
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.workout_app_2.ui.theme.Workout_App_2Theme

@Composable
fun ExerciseItemItem(
    exerciseData: ExerciseData
){
    Surface(modifier = Modifier
        .padding(bottom = 0.dp)
        .height(45.dp)
        .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)

    ) {
        Row(modifier = Modifier
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .clip(RoundedCornerShape(40))
            .padding(horizontal = 8.dp)
            .padding(bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            BasicTextField(
                modifier = Modifier
                    .weight(2f)
                    .padding(end = 4.dp)
                    .height(35.dp)
                    .clip(RoundedCornerShape(40))
                    .background(MaterialTheme.colorScheme.primary.copy(0.4f)),
                value = exerciseData.firstPart,
                singleLine = true,
                enabled = false,
                onValueChange = {},
                textStyle = TextStyle(textAlign = TextAlign.Center, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        innerTextField()
                    }
                }
            )
            BasicTextField(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp)
                    .height(35.dp)
                    .clip(RoundedCornerShape(40))
                    .background(MaterialTheme.colorScheme.primary.copy(0.4f)),
                value = exerciseData.secondPart,
                enabled = false,
                onValueChange = {},
                textStyle = TextStyle(textAlign = TextAlign.Center, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        innerTextField()
                    }
                }
            )
        }
    }
}

@Preview
@Composable
fun PreviewExerciseItemItem(){
    Workout_App_2Theme {
        ExerciseItemItem(exerciseData = ExerciseData("Highest 1RM", "100 KG"))
    }
}