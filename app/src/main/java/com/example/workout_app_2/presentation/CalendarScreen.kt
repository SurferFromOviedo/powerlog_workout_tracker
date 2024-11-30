package com.example.workout_app_2.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowLeft
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
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
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

data class MonthData(val monthName: String, val daysInMonth: Int, val firstDayOfWeek: Int, val monthNumber: Int, val indicators: Map<Int, List<Color>>)

fun getYearTimestamps(year: Int): Pair<Long, Long> {
    val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    calendar.set(year, Calendar.JANUARY, 1, 0, 0, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    val startTimestamp = calendar.timeInMillis

    calendar.set(year, Calendar.DECEMBER, 31, 23, 59, 59)
    calendar.set(Calendar.MILLISECOND, 999)
    val endTimestamp = calendar.timeInMillis

    return Pair(startTimestamp, endTimestamp)
}

fun getMonthsData(year: Int): List<MonthData>{
    val calendar  = Calendar.getInstance()
    calendar.set(Calendar.YEAR, year)
    return List(12){ month ->
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        MonthData(
            monthName = SimpleDateFormat("MMMM", Locale.getDefault()).format(calendar.time),
            daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH),
            firstDayOfWeek = (calendar.get(Calendar.DAY_OF_WEEK) + 5) % 7,
            monthNumber = month + 1,
            indicators = emptyMap()
        )
    }
}

fun getColorForBodyPart(bodyPart: String, darkTheme: Boolean): Color {
    when (bodyPart) {
        "Chest" -> {
            return YellowIndicator
        }
        "Back" -> {
            return BlueIndicator
        }
        "Legs" -> {
            return if(darkTheme){
                Color.White
            }else{
                Color.Black
            }
        }
        "Shoulders" -> {
            return GreenIndicator
        }
        "Whole Body" -> {
            return PinkIndicator
        }
        "Forearms" -> {
            return PurpleIndicator
        }
        "Core" -> {
            return OrangeIndicator
        }
        else -> {
            return RedIndicator
        }
    }
}

fun getIndicatorsForMonth(
    workoutEntities: List<WorkoutEntity>,
    workoutExercises: List<WorkoutExerciseEntity>,
    exercises: List<ExerciseEntity>,
    month: Int,
    year: Int,
    darkTheme: Boolean
): Map<Int, List<Color>> {

    val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    val indicators = mutableMapOf<Int, MutableList<Color>>()

    val workoutsForMonth = workoutEntities.filter { workout ->
        calendar.timeInMillis = workout.startTime
        val workoutMonth = calendar.get(Calendar.MONTH) + 1
        val workoutYear = calendar.get(Calendar.YEAR)
        workoutMonth == month && workoutYear == year
    }

    for (workout in workoutsForMonth) {
        val workoutExercisesForWorkout = workoutExercises.filter { it.workoutId == workout.id }
            .map { workoutExercise ->
                exercises.find { it.id == workoutExercise.exerciseId }
            }

        calendar.timeInMillis = workout.startTime
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        val bodyPartsColors = workoutExercisesForWorkout.mapNotNull { exercise ->
            exercise?.let {
                getColorForBodyPart(it.bodyPart, darkTheme)
            }
        }.distinct()

        if (indicators.containsKey(dayOfMonth)) {
            val existingColors = indicators[dayOfMonth] ?: mutableListOf()
            val newColors = bodyPartsColors.filterNot { existingColors.contains(it) }
            indicators[dayOfMonth]?.addAll(newColors)
        } else {
            indicators[dayOfMonth] = bodyPartsColors.toMutableList()
        }
    }
    return indicators
}

fun getWorkoutsForDay(
    day: Int,
    month: Int,
    year: Int,
    workoutEntities: List<WorkoutEntity>
): List<WorkoutEntity> {
    val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))

    calendar.set(Calendar.YEAR, year)
    calendar.set(Calendar.MONTH, month - 1)
    calendar.set(Calendar.DAY_OF_MONTH, day)
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)

    val startOfDayInMillis = calendar.timeInMillis

    calendar.set(Calendar.HOUR_OF_DAY, 23)
    calendar.set(Calendar.MINUTE, 59)
    calendar.set(Calendar.SECOND, 59)
    calendar.set(Calendar.MILLISECOND, 999)

    val endOfDayInMillis = calendar.timeInMillis

    return workoutEntities.filter { workout ->
        workout.startTime in startOfDayInMillis..endOfDayInMillis
    }
}



@Composable
fun CalendarScreen(
    preferencesViewModel: PreferencesViewModel
){
    val context = LocalContext.current
    val database = remember { DatabaseProvider.getDatabase(context) }
    val todayYear = SimpleDateFormat("yyyy", Locale.getDefault()).format(Date()).toInt()
    val todayMonth = SimpleDateFormat("MM", Locale.getDefault()).format(Date()).toInt()
    val year = remember { mutableIntStateOf(todayYear) }

    val unitStore by preferencesViewModel.unitFlow.collectAsState()
    val themeStore by preferencesViewModel.themeFlow.collectAsState()
    val primaryColorStore by preferencesViewModel.primaryColorFlow.collectAsState()
    val dynamicColorStore by preferencesViewModel.dynamicColorFlow.collectAsState()
    val screenOnStore by preferencesViewModel.screenOnFlow.collectAsState()

    val theme by remember {mutableStateOf(themeStore) }
    val unit by remember {mutableStateOf(unitStore) }
    val primaryColor by remember { mutableIntStateOf(primaryColorStore) }
    val dynamicColor by remember { mutableStateOf(dynamicColorStore) }
    val screenOn by remember { mutableStateOf(screenOnStore) }

    val isLoading = remember { mutableStateOf(true) }
    val isScrollDone = remember { mutableStateOf(false) }

    val darkTheme = when (theme) {
        "Dark" -> true
        "Light" -> false
        else -> isSystemInDarkTheme()
    }

    val workoutEntities = remember { mutableStateListOf<WorkoutEntity>() }
    val workoutExercises = remember { mutableStateListOf<WorkoutExerciseEntity>() }
    val exercises = remember { mutableStateListOf<ExerciseEntity>() }

    val scope = rememberCoroutineScope()
    val calendarData = remember { mutableStateListOf<MonthData>() }

    val listState = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }

    val selectedDay = remember { mutableStateOf<Int?>(null) }
    val selectedMonth = remember { mutableStateOf<Int?>(null) }

    val showBottomSheet = remember { mutableStateOf(false) }
    var showInfo by rememberSaveable { mutableStateOf(false) }

    val workoutEntitiesForDay = remember { mutableStateListOf<WorkoutEntity>() }

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

    SnackbarHost(hostState = snackbarHostState)
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
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
                    .fillMaxSize(),
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
                            selectedDay.value = day
                            selectedMonth.value = calendarData[monthData].monthNumber
                            workoutEntitiesForDay.clear()
                            workoutEntitiesForDay.addAll(getWorkoutsForDay(selectedDay.value!!, selectedMonth.value!!, year.intValue, workoutEntities))
                            showBottomSheet.value = true
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

        if (showBottomSheet.value) {
            if(!workoutEntitiesForDay.isEmpty()){
                CalendarSheet(
                    workoutEntities = workoutEntitiesForDay,
                    onDismissRequest = { showBottomSheet.value = false },
                    onWorkoutDelete = {
                        scope.launch {
                            workoutEntities.clear()
                            workoutEntities.addAll(database.workoutDao().getAllWorkouts())

                            workoutExercises.clear()
                            workoutExercises.addAll(database.workoutDao().getAllWorkoutExercises())

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
                            workoutEntitiesForDay.clear()
                            workoutEntitiesForDay.addAll(getWorkoutsForDay(selectedDay.value!!, selectedMonth.value!!, year.intValue, workoutEntities))
                            showBottomSheet.value = false
                            snackbarHostState.showSnackbar("Workout deleted successfully")
                        }
                    },
                    onWorkoutEdit = {
                        scope.launch {
                            workoutEntities.clear()
                            workoutEntities.addAll(database.workoutDao().getAllWorkouts())

                            workoutExercises.clear()
                            workoutExercises.addAll(database.workoutDao().getAllWorkoutExercises())

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
                            workoutEntitiesForDay.clear()
                            workoutEntitiesForDay.addAll(getWorkoutsForDay(selectedDay.value!!, selectedMonth.value!!, year.intValue, workoutEntities))
                            snackbarHostState.showSnackbar("Workout edited successfully")
                        }
                    },
                    unit = unit,
                    theme = theme,
                    dynamicColor = dynamicColor,
                    primaryColor = primaryColor,
                    screenOn = screenOn
                )
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
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun CalendarScreenPreview(){
    Workout_App_2Theme {
        //CalendarScreen(preferencesViewModel = PreferencesViewModel(PreferencesDataStore(LocalContext.current)))
    }
}