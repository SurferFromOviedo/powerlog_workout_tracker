package com.example.workout_app_2.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
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
fun CalendarItem(
    monthName: String,
    daysInMonth: Int,
    firstDayOfWeek: Int,
    indicators: Map<Int, List<Color>>,
    onDayClick: (Int) -> Unit
){
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 4.dp
    ){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primary.copy(0.2f)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding (top = 8.dp),
                text = monthName,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleLarge
            )
            LazyVerticalGrid(
                modifier = Modifier
                    .fillMaxSize(),
                columns = GridCells.Fixed(7),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.SpaceEvenly,
                userScrollEnabled = false
            ){
                items(firstDayOfWeek){
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                    )
                }

                items(daysInMonth){ day->
                    CalendarDay(
                        day = day + 1,
                        indicators = indicators[day + 1] ?: emptyList(),
                        onDayClick = onDayClick
                    )
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
fun CalendarItemPreview(){
    val daysInMonth = 31
    Workout_App_2Theme {
        CalendarItem(monthName = "January", daysInMonth = daysInMonth, firstDayOfWeek = 1, indicators = emptyMap(), onDayClick = {})
    }
}
