package com.example.workout_app_2.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.workout_app_2.ui.theme.Workout_App_2Theme

@Composable
fun CalendarDay(
    day: Int,
    indicators: List<Color>,
    onDayClick: (Int) -> Unit
){
    Column(
        modifier = Modifier
            .size(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Surface(
            modifier = Modifier
                .size(40.dp),
            shape = CircleShape
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clickable { onDayClick(day) },
                contentAlignment = Alignment.Center,
            ){
                Surface(
                    modifier = Modifier
                        .size(40.dp),
                    shape = CircleShape
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surface,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = day.toString(),
                            color = MaterialTheme.colorScheme.onSurface)
                    }
                }
                if(indicators.isNotEmpty()){
                    var circleSize = 40
                    indicators.take(4).forEach { indicator ->
                        Box(
                            modifier = Modifier
                                .size(circleSize.dp)

                                .border(
                                    width = 2.dp,
                                    color = indicator,
                                    shape = CircleShape

                                )
                        )
                        circleSize -= 4
                    }
                }
            }
        }


    }
}

@Preview
@Composable
fun CalendarDayPreview(){
    Workout_App_2Theme {
        CalendarDay(day = 1, indicators = listOf(Color.Red, Color.Blue),  onDayClick = {})
    }
}