package com.example.workout_app_2.presentation

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowLeft
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.workout_app_2.data.DatabaseProvider
import com.example.workout_app_2.data.ExerciseEntity
import com.example.workout_app_2.data.PreferencesDataStore
import com.example.workout_app_2.data.PreferencesViewModel
import com.example.workout_app_2.data.WorkoutEntity
import com.example.workout_app_2.data.WorkoutExerciseEntity
import com.example.workout_app_2.ui.theme.BlueIndicator
import com.example.workout_app_2.ui.theme.GreenIndicator
import com.example.workout_app_2.ui.theme.OrangeIndicator
import com.example.workout_app_2.ui.theme.PinkIndicator
import com.example.workout_app_2.ui.theme.PurpleIndicator
import com.example.workout_app_2.ui.theme.RedIndicator
import com.example.workout_app_2.ui.theme.Workout_App_2Theme
import com.example.workout_app_2.ui.theme.YellowIndicator
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.Calendar

@Composable
fun DateRangePicker(
    preferencesViewModel: PreferencesViewModel,
    onDismiss: () -> Unit,
    onDateRangeSelected: (Pair<Long, Long>) -> Unit
){

    val context = LocalContext.current
    val database = remember { DatabaseProvider.getDatabase(context) }
    val todayYear = SimpleDateFormat("yyyy", Locale.getDefault()).format(Date()).toInt()
    val todayMonth = SimpleDateFormat("MM", Locale.getDefault()).format(Date()).toInt()
    val year = remember { mutableIntStateOf(todayYear) }

    val isAllTime = remember { mutableStateOf(false) }

    val themeStore by preferencesViewModel.themeFlow.collectAsState()
    val theme by remember {mutableStateOf(themeStore) }

    val darkTheme = when (theme) {
        "Dark" -> true
        "Light" -> false
        else -> isSystemInDarkTheme()
    }

    val calendarData = remember { mutableStateListOf<MonthData>() }
    val workoutEntities = remember { mutableStateListOf<WorkoutEntity>() }
    val workoutExercises = remember { mutableStateListOf<WorkoutExerciseEntity>() }
    val exercises = remember { mutableStateListOf<ExerciseEntity>() }

    val selectedStartDate = remember { mutableStateOf<Long?>(null) }
    val selectedEndDate = remember { mutableStateOf<Long?>(null) }

    val isLoading = remember { mutableStateOf(true) }
    val isScrollDone = remember { mutableStateOf(false) }

    val listState = rememberLazyListState()

    var showInfo by rememberSaveable { mutableStateOf(false) }

    val selectedText = remember {
        derivedStateOf {
            val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
            if(isAllTime.value){
                "All time"
            }else{
                if(selectedStartDate.value != null && selectedEndDate.value != null){
                    val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
                        timeInMillis = selectedEndDate.value!!
                    }
                    val endDay = calendar.get(Calendar.DAY_OF_MONTH)
                    val endMonth = calendar.get(Calendar.MONTH) + 1
                    val endYear = calendar.get(Calendar.YEAR)
                    val startDateForEndDate = getStartOfTheDay(endDay, endMonth, endYear)
                    val startDate = dateFormat.format(selectedStartDate.value!!)
                    val endDate = dateFormat.format(selectedEndDate.value!!)

                    if(selectedStartDate.value == startDateForEndDate) {
                        startDate
                    }else{
                        "$startDate - $endDate"
                    }
                }else{
                    "Select dates"
                }
            }
        }
    }

    LaunchedEffect(Unit){
        listState.scrollToItem(todayMonth - 1)
        delay(250)
        isScrollDone.value = true
    }

    LaunchedEffect(database, year.intValue) {
        isLoading.value = true
        val (startTimestamp, endTimestamp) = getYearTimestamps(year.intValue)
        workoutEntities.clear()
        workoutEntities.addAll(database.workoutDao().getAllWorkoutsBetweenTimestamps(startTimestamp, endTimestamp))

        workoutExercises.clear()
        workoutExercises.addAll(database.workoutDao().getAllWorkoutExercisesBetweenTimestamps(startTimestamp, endTimestamp))

        exercises.clear()
        exercises.addAll(database.exerciseDao().getAllExercises())

        val monthsData = getMonthsData(year.intValue).map { monthData ->
            val indicators = getIndicatorsForMonth(
                workoutEntities = workoutEntities,
                workoutExercises = workoutExercises,
                exercises = exercises,
                month = monthData.monthNumber,
                year = year.intValue,
                darkTheme = darkTheme
            )
            monthData.copy(indicators = indicators)
        }
        calendarData.clear()
        calendarData.addAll(monthsData)
        delay(250)
        isLoading.value = false
    }

    val onRangeSelected = { range: String ->
        val calendar = Calendar.getInstance()

        when(range){
            "Today" -> {
                isAllTime.value = false
               selectedStartDate.value = getStartOfTheDay(
                   calendar.get(Calendar.DAY_OF_MONTH),
                   calendar.get(Calendar.MONTH) + 1,
                   calendar.get(Calendar.YEAR)
               )
                selectedEndDate.value = getEndOfTheDay(
                    calendar.get(Calendar.DAY_OF_MONTH),
                    calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.YEAR)
                )
            }
            "Last 7 days" -> {
                isAllTime.value = false
                selectedEndDate.value = getEndOfTheDay(
                    calendar.get(Calendar.DAY_OF_MONTH),
                    calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.YEAR)
                )
                calendar.add(Calendar.DAY_OF_MONTH, -6)
                selectedStartDate.value = getStartOfTheDay(
                    calendar.get(Calendar.DAY_OF_MONTH),
                    calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.YEAR)
                )
            }
            "Last 14 days" -> {
                isAllTime.value = false
                selectedEndDate.value = getEndOfTheDay(
                    calendar.get(Calendar.DAY_OF_MONTH),
                    calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.YEAR)
                )
                calendar.add(Calendar.DAY_OF_YEAR, -13)
                selectedStartDate.value = getStartOfTheDay(
                    calendar.get(Calendar.DAY_OF_MONTH),
                    calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.YEAR)
                )
            }
            "Last 30 days" -> {
                isAllTime.value = false
                selectedEndDate.value = getEndOfTheDay(
                    calendar.get(Calendar.DAY_OF_MONTH),
                    calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.YEAR)
                )
                calendar.add(Calendar.DAY_OF_YEAR, -29)
                selectedStartDate.value = getStartOfTheDay(
                    calendar.get(Calendar.DAY_OF_MONTH),
                    calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.YEAR)
                )
            }
            "Previous month" -> {
                isAllTime.value = false
                calendar.add(Calendar.MONTH, -1)
                selectedStartDate.value = getStartOfTheDay(1, calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR))
                selectedEndDate.value = getEndOfTheDay(
                    calendar.getActualMaximum(Calendar.DAY_OF_MONTH),
                    calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.YEAR)
                )
            }
            "Last 90 days" -> {
                isAllTime.value = false
                selectedEndDate.value = getEndOfTheDay(
                    calendar.get(Calendar.DAY_OF_MONTH),
                    calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.YEAR)
                )
                calendar.add(Calendar.DAY_OF_YEAR, -89)
                selectedStartDate.value = getStartOfTheDay(
                    calendar.get(Calendar.DAY_OF_MONTH),
                    calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.YEAR)
                )
            }
            "Last 180 days" -> {
                isAllTime.value = false
                selectedEndDate.value = getEndOfTheDay(
                    calendar.get(Calendar.DAY_OF_MONTH),
                    calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.YEAR)
                )
                calendar.add(Calendar.DAY_OF_YEAR, -179)
                selectedStartDate.value = getStartOfTheDay(
                    calendar.get(Calendar.DAY_OF_MONTH),
                    calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.YEAR)
                )
            }
            "Last year" -> {
                isAllTime.value = false
                selectedEndDate.value = getEndOfTheDay(
                    calendar.get(Calendar.DAY_OF_MONTH),
                    calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.YEAR)
                )
                calendar.add(Calendar.YEAR, -1)
                selectedStartDate.value = getStartOfTheDay(
                    calendar.get(Calendar.DAY_OF_MONTH),
                    calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.YEAR)
                )
            }
            "Previous year" -> {
                isAllTime.value = false
                calendar.add(Calendar.YEAR, -1)
                selectedStartDate.value = getStartOfTheDay(1, 1, calendar.get(Calendar.YEAR))
                selectedEndDate.value = getEndOfTheDay(
                    calendar.getActualMaximum(Calendar.DAY_OF_MONTH),
                    12,
                    calendar.get(Calendar.YEAR)
                )
            }
            "All time" -> {
                selectedStartDate.value = getStartOfTheDay(1, 1, 1970)
                selectedEndDate.value = getEndOfTheDay(
                    calendar.get(Calendar.DAY_OF_MONTH),
                    calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.YEAR)
                )
                isAllTime.value = true
            }
        }
    }

    val onDaySelected = { day: Int, month: Int, yearOfDay: Int ->
        isAllTime.value = false
        if(selectedStartDate.value == null){
            selectedStartDate.value = getStartOfTheDay(day, month, yearOfDay)
            selectedEndDate.value = getEndOfTheDay(day, month, yearOfDay)
        }else{
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
                    timeInMillis = selectedEndDate.value!!
                }
            val endDay = calendar.get(Calendar.DAY_OF_MONTH)
            val endMonth = calendar.get(Calendar.MONTH) + 1
            val endYear = calendar.get(Calendar.YEAR)
            val startDate = getStartOfTheDay(endDay, endMonth, endYear)

            if(selectedStartDate.value == startDate) {
                if (getStartOfTheDay(day, month, yearOfDay) >= selectedStartDate.value!!) {
                selectedEndDate.value = getEndOfTheDay(day, month, yearOfDay)
            } else if(getStartOfTheDay(day, month, yearOfDay) < selectedStartDate.value!!) {
                val calendar2 = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
                    timeInMillis = selectedStartDate.value!!
                }
                val startDay = calendar2.get(Calendar.DAY_OF_MONTH)
                val startMonth = calendar2.get(Calendar.MONTH) + 1
                val startYear = calendar2.get(Calendar.YEAR)
                selectedEndDate.value = getEndOfTheDay(startDay, startMonth, startYear)

                selectedStartDate.value = getStartOfTheDay(day, month, yearOfDay)
                }
            }else{
                selectedStartDate.value = getStartOfTheDay(day, month, yearOfDay)
                selectedEndDate.value = getEndOfTheDay(day, month, yearOfDay)
            }
        }
    }


    Dialog(
        onDismissRequest = {onDismiss()},
        DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true, usePlatformDefaultWidth = false)
    ){
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.7f),

            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ){
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp)
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically

                ){
                    IconButton(
                        modifier = Modifier
                            .size(48.dp),
                        onClick = {
                            onDismiss()
                        }
                    ) {
                        
                        Icon(
                            modifier = Modifier
                                .size(30.dp),
                            imageVector = Icons.Outlined.Close,
                            contentDescription = "Close"
                        )
                    }


                    Text(
                        modifier = Modifier,
                        text = selectedText.value,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(
                        modifier = Modifier
                            .size(48.dp),
                        onClick = {
                            onDateRangeSelected(Pair(selectedStartDate.value!!, selectedEndDate.value!!))
                        },
                        enabled = selectedStartDate.value != null && selectedEndDate.value != null
                    ) {
                        Icon(
                            modifier = Modifier
                                .size(30.dp),
                            imageVector = Icons.Outlined.CheckCircleOutline,
                            contentDescription = "Select"
                        )
                    }
                }


                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    val timeRanges = listOf(
                        "All time",
                        "Today",
                        "Last 7 days",
                        "Last 14 days",
                        "Last 30 days",
                        "Previous month",
                        "Last 90 days",
                        "Last 180 days",
                        "Last year",
                        "Previous year"
                    )

                    items(timeRanges.size) { index ->
                        FilledTonalButton(
                            modifier = Modifier
                                .height(40.dp),
                            onClick = {
                                onRangeSelected(timeRanges[index])
                                Log.d("DateRangePicker", "Start date: $selectedStartDate End date: $selectedEndDate")
                            },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = timeRanges[index],
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Box(modifier = Modifier.size(48.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ){
                        IconButton(
                            onClick = {
                                if (year.intValue > 1970) {
                                    year.intValue--
                                }
                            },
                            enabled = year.intValue > 1970
                        ) {
                            Icon(
                                modifier = Modifier
                                    .size(48.dp),
                                imageVector = Icons.AutoMirrored.Filled.ArrowLeft,
                                contentDescription = "Previous year"
                            )
                        }

                        Text(
                            text = year.intValue.toString(),
                            fontSize = MaterialTheme.typography.titleLarge.fontSize
                        )

                        IconButton(
                            onClick = {
                                year.intValue++
                            }
                        ) {
                            Icon(
                                modifier = Modifier
                                    .size(48.dp),
                                imageVector = Icons.AutoMirrored.Filled.ArrowRight,
                                contentDescription = "Next year"
                            )
                        }
                    }



                    IconButton(
                        modifier = Modifier
                            .size(48.dp),
                        onClick = {
                            showInfo = true
                        }
                    ) {
                        Icon(
                            modifier = Modifier
                                .size(24.dp),
                            imageVector = Icons.Outlined.Info,
                            contentDescription = "Information"
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text(text = "M")
                    Text(text = "T")
                    Text(text = "W")
                    Text(text = "T")
                    Text(text = "F")
                    Text(text = "S")
                    Text(text = "S")
                }
                HorizontalDivider()

                if(!isLoading.value) {
                    if(!isScrollDone.value) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(color = MaterialTheme.colorScheme.background),
                            contentAlignment = Alignment.Center
                        ){
                            CircularProgressIndicator()
                        }
                    }
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth(),
                        state = listState,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ) {
                        items(calendarData.size) { monthData ->
                            CalendarItem(
                                monthName = calendarData[monthData].monthName,
                                daysInMonth = calendarData[monthData].daysInMonth,
                                firstDayOfWeek = calendarData[monthData].firstDayOfWeek,
                                indicators = calendarData[monthData].indicators,
                                onDayClick = { day ->
                                    onDaySelected(day, calendarData[monthData].monthNumber, year.intValue)
                                    Log.d("DateRangePicker", "Start date: $selectedStartDate End date: $selectedEndDate")
                                }
                            )
                        }
                    }

                }else{
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

            }

        }
    }

    if (showInfo) {
        Dialog(onDismissRequest = { showInfo = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.background)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ){
                    Text(
                        modifier = Modifier
                            .padding(8.dp),
                        text = "Indicators",
                        fontSize = 18.sp
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically

                    ){
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ){
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .background(shape = CircleShape, color = if(darkTheme) Color.White else Color.Black)
                            )
                            Text(text = "Legs", fontSize = 12.sp)
                        }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ){
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .background(shape = CircleShape, color = BlueIndicator)
                            )
                            Text(text = "Back", fontSize = 12.sp)
                        }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ){
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .background(shape = CircleShape, color = YellowIndicator)
                            )
                            Text(text = "Chest", fontSize = 12.sp)
                        }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ){
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .background(shape = CircleShape, color = GreenIndicator)
                            )
                            Text(text = "Shoulders", fontSize = 12.sp)
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically

                    ){
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ){
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .background(shape = CircleShape, color = RedIndicator)
                            )
                            Text(text = "Arms", fontSize = 12.sp)
                        }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ){
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .background(shape = CircleShape, color = OrangeIndicator)
                            )
                            Text(text = "Core", fontSize = 12.sp)
                        }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ){
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .background(shape = CircleShape, color = PurpleIndicator)
                            )
                            Text(text = "Forearms", fontSize = 12.sp)
                        }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ){
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .background(shape = CircleShape, color = PinkIndicator)
                            )
                            Text(text = "Whole Body", fontSize = 12.sp)
                        }
                    }
                }

            }
        }
    }
}

@Preview
@Composable
fun DateRangePickerPreview(){
    Workout_App_2Theme {
        DateRangePicker(onDismiss = {}, preferencesViewModel = PreferencesViewModel(PreferencesDataStore(LocalContext.current)), onDateRangeSelected = { })
    }
}