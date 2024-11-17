package com.example.workout_app_2.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.workout_app_2.data.ExerciseSet
import com.example.workout_app_2.ui.theme.Workout_App_2Theme
import java.text.DecimalFormat

@Composable
fun SetItemForCalendarSheet(
    set: ExerciseSet,
    unitString: String?
){
    var weight by remember { mutableStateOf(set.weight) }
    if (weight.isEmpty()) {
        weight = "0"
    }
    var unit by remember { mutableStateOf(set.unit ?: unitString) }
    val reps by remember { mutableStateOf(set.reps) }
    val df = DecimalFormat("#.##")

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
                    .background(MaterialTheme.colorScheme.primary.copy(0.4f)),
                value = df.format(weight.toDouble()),
                onValueChange = {},
                enabled = false,
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
                }
            )

            Button(modifier = Modifier
                .size(35.dp),
                onClick = {
                    unit = if (unit == "KG") "LB" else "KG"
                    weight = if (unit == "KG") {
                        (weight.toDouble() / 2.20462262185).toString()
                    } else {
                        (weight.toDouble() * 2.20462262185).toString()
                    }
                },
                shape = RoundedCornerShape(40),
                colors =  ButtonDefaults.buttonColors(containerColor = colorScheme.primary.copy(0.3f)),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(modifier = Modifier,
                    text = unit!!,
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
                onValueChange = {},
                enabled = false,
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
fun PreviewSetItemForCalendarSheet(){
    val fakeSet = ExerciseSet("100", "KG", "10")
    Workout_App_2Theme {
        SetItemForCalendarSheet(fakeSet,"KG")
    }
}