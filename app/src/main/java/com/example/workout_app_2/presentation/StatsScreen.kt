package com.example.workout_app_2.presentation

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Scale
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.workout_app_2.data.DatabaseProvider
import com.example.workout_app_2.data.Exercise
import com.example.workout_app_2.data.ExerciseEntity
import com.example.workout_app_2.data.ExerciseRepository
import com.example.workout_app_2.data.PreferencesDataStore
import com.example.workout_app_2.data.PreferencesViewModel
import com.example.workout_app_2.data.SetEntity
import com.example.workout_app_2.data.WorkoutEntity
import com.example.workout_app_2.data.WorkoutExerciseEntity
import com.example.workout_app_2.ui.theme.Workout_App_2Theme
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieEntry
import com.google.accompanist.pager.HorizontalPagerIndicator
import kotlinx.coroutines.delay
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit
import kotlin.math.exp
import kotlin.math.pow

data class VolumeData(
    val exerciseName: String,
    val totalVolume: Double,
    val totalSets: Int
)
data class ExerciseData(
    val firstPart: String,
    val secondPart: String
)

data class StatsItemData(
    val icon: ImageVector,
    val value: String,
    val label: String,
    val date: String? = null
)
fun getDaysForChart(baseTime: Long, startTime: Long): Float {
    val utcTimeZone = TimeZone.getTimeZone("UTC")

    val baseDate = Calendar.getInstance(utcTimeZone).apply {
        timeInMillis = baseTime

        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    val startDate = Calendar.getInstance(utcTimeZone).apply {
        timeInMillis = startTime
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    return ((startDate.timeInMillis - baseDate.timeInMillis) / (24 * 60 * 60 * 1000)).toFloat()
}

fun getStartOfTheDay(
    day: Int,
    month: Int,
    year: Int
): Long{
    val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))

    calendar.set(Calendar.YEAR, year)
    calendar.set(Calendar.MONTH, month - 1)
    calendar.set(Calendar.DAY_OF_MONTH, day)
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)

    val startOfDayInMillis = calendar.timeInMillis
    return startOfDayInMillis
}

fun getEndOfTheDay(
    day: Int,
    month: Int,
    year: Int
): Long{
    val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))

    calendar.set(Calendar.YEAR, year)
    calendar.set(Calendar.MONTH, month - 1)
    calendar.set(Calendar.DAY_OF_MONTH, day)
    calendar.set(Calendar.HOUR_OF_DAY, 23)
    calendar.set(Calendar.MINUTE, 59)
    calendar.set(Calendar.SECOND, 59)
    calendar.set(Calendar.MILLISECOND, 999)

    val endOfDayInMillis = calendar.timeInMillis
    return endOfDayInMillis
}

fun fillStatsItemDataListsList(
    totalTime: Long,
    avgTime: Long,
    maxTime: Long,
    minTime: Long,
    maxTimeDate: Long?,
    minTimeDate: Long?,
    totalVolume: Int,
    avgVolume: Int,
    maxVolume: Int,
    minVolume: Int,
    maxVolumeDate: Long?,
    minVolumeDate: Long?,
    totalExercises: Int,
    avgExercises: Int,
    maxExercises: Int,
    minExercises: Int,
    maxExercisesDate: Long?,
    minExercisesDate: Long?,
    totalSets: Int,
    avgSets: Int,
    maxSets: Int,
    minSets: Int,
    maxSetsDate: Long?,
    minSetsDate: Long?,
    totalReps: Int,
    avgReps: Int,
    maxReps: Int,
    minReps: Int,
    maxRepsDate: Long?,
    minRepsDate: Long?,
    unit: String

): List<List<StatsItemData>>{
    fun convertTime(time: Long): String{
        val hours = TimeUnit.MILLISECONDS.toHours(time)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(time) % 60
        return String.format("%02dH %02dM", hours, minutes)
    }
    fun convertDate(date: Long?): String{
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        return if(date == null){
            ""
        }else{
            dateFormat.format(date)
        }
    }
    val statsItemDataListsList = mutableStateListOf<List<StatsItemData>>()
    statsItemDataListsList.addAll(
        listOf(
            listOf(
                StatsItemData(
                    icon = Icons.Outlined.AccessTime,
                    value = convertTime(totalTime),
                    label = "Total time"
                ),
                StatsItemData(
                    icon = Icons.Outlined.AccessTime,
                    value = convertTime(avgTime),
                    label = "Average time"
                ),
                StatsItemData(
                    icon = Icons.Outlined.AccessTime,
                    value = convertTime(maxTime),
                    label = "Max time",
                    date = convertDate(maxTimeDate)
                ),
                StatsItemData(
                    icon = Icons.Outlined.AccessTime,
                    value = convertTime(minTime),
                    label = "Min time",
                    date = convertDate(minTimeDate)
                )
            ),
            listOf(
                StatsItemData(
                    icon = Icons.Outlined.Scale,
                    value = "$totalVolume $unit",
                    label = "Total volume"
                ),
                StatsItemData(
                    icon = Icons.Outlined.Scale,
                    value = "$avgVolume $unit",
                    label = "Average volume"
                ),
                StatsItemData(
                    icon = Icons.Outlined.Scale,
                    value = "$maxVolume $unit",
                    label = "Max volume",
                    date = convertDate(maxVolumeDate)
                ),
                StatsItemData(
                    icon = Icons.Outlined.Scale,
                    value = "$minVolume $unit",
                    label = "Min volume",
                    date = convertDate(minVolumeDate)
                )
            ),
            listOf(
                StatsItemData(
                    icon = Icons.Default.FitnessCenter,
                    value = "$totalExercises",
                    label = "Total exercises"
                ),
                StatsItemData(
                    icon = Icons.Default.FitnessCenter,
                    value = "$avgExercises",
                    label = "Average exercises"
                ),
                StatsItemData(
                    icon = Icons.Default.FitnessCenter,
                    value = "$maxExercises",
                    label = "Max exercises",
                    date = convertDate(maxExercisesDate)
                ),
                StatsItemData(
                    icon = Icons.Default.FitnessCenter,
                    value = "$minExercises",
                    label = "Min exercises",
                    date = convertDate(minExercisesDate)
                )
            ),
            listOf(
                StatsItemData(
                    icon = Icons.Default.FitnessCenter,
                    value = "$totalSets",
                    label = "Total sets"
                ),
                StatsItemData(
                    icon = Icons.Default.FitnessCenter,
                    value = "$avgSets",
                    label = "Average sets"
                ),
                StatsItemData(
                    icon = Icons.Default.FitnessCenter,
                    value = "$maxSets",
                    label = "Max sets",
                    date = convertDate(maxSetsDate)
                ),
                StatsItemData(
                    icon = Icons.Default.FitnessCenter,
                    value = "$minSets",
                    label = "Min sets",
                    date = convertDate(minSetsDate)
                )
            ),
            listOf(
                StatsItemData(
                    icon = Icons.Default.FitnessCenter,
                    value = "$totalReps",
                    label = "Total reps"
                ),
                StatsItemData(
                    icon = Icons.Default.FitnessCenter,
                    value = "$avgReps",
                    label = "Average reps"
                ),
                StatsItemData(
                    icon = Icons.Default.FitnessCenter,
                    value = "$maxReps",
                    label = "Max reps",
                    date = convertDate(maxRepsDate)
                ),
                StatsItemData(
                    icon = Icons.Default.FitnessCenter,
                    value = "$minReps",
                    label = "Min reps",
                    date = convertDate(minRepsDate)
                )
            )
        )
    )
    return statsItemDataListsList
}

fun convertWeight(weight: String, unit: String, defaultUnit: String): Double{
    val weightValue = if (weight.isEmpty()) 0.0 else weight.toDouble()
    return if(unit != defaultUnit){
        when (unit) {
            "LB" -> {
                (weightValue / 2.20462262185)
            }

            else -> {
                (weightValue * 2.20462262185)
            }
        }
    }else{
        weightValue
    }
}


@Composable
fun StatsScreen(
    preferencesViewModel: PreferencesViewModel
){
    val showSelectDateDialog = remember { mutableStateOf(false) }
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = {2}
    )
    val dateFormat = remember {
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
    }
    val calendar = remember{ Calendar.getInstance() }

    val todayTimeStamp = remember {
        getEndOfTheDay(
            calendar.get(Calendar.DAY_OF_MONTH),
            calendar.get(Calendar.MONTH) + 1,
            calendar.get(Calendar.YEAR)
        )
    }

    val endDateTime = rememberSaveable { mutableStateOf<Long?>(todayTimeStamp) }

    val startDateTime = rememberSaveable {
        calendar.add(Calendar.DAY_OF_YEAR, -6)
        mutableStateOf<Long?>(
            getStartOfTheDay(
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.YEAR)
            )
        )
    }
    var selectedTimePeriod by remember {
        mutableStateOf("${dateFormat.format(startDateTime.value)} - ${dateFormat.format(endDateTime.value)}")
    }

    LaunchedEffect(Unit){
        if(startDateTime.value == getStartOfTheDay(1, 1, 1970) && endDateTime.value == todayTimeStamp){
            selectedTimePeriod = "All time"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ){
        OutlinedTextField(
            value = selectedTimePeriod,
            onValueChange = { },
            label = { Text("Date range") },
            placeholder = { Text("MM/DD/YYYY") },
            trailingIcon = {
                Icon(Icons.Default.DateRange, contentDescription = "Select date")
            },
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .padding(horizontal = 16.dp)
                .pointerInput(selectedTimePeriod) {
                    awaitEachGesture {
                        awaitFirstDown(pass = PointerEventPass.Initial)
                        val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                        if (upEvent != null) {
                            showSelectDateDialog.value = true
                        }
                    }
                }
        )

        Box(
            modifier = Modifier
                .weight(1f)
        ){
            HorizontalPager(state = pagerState) { page ->
                when(page){
                    0 -> StatsPage1(preferencesViewModel, endDateTime, startDateTime)
                    1 -> StatsPage2(preferencesViewModel, endDateTime, startDateTime)
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
        ){
            HorizontalPagerIndicator(
                modifier = Modifier
                    .align(Alignment.BottomCenter),
                pagerState = pagerState,
                pageCount = 2,
                activeColor = MaterialTheme.colorScheme.primary
            )
        }
    }
    if(showSelectDateDialog.value){
        DateRangePicker(
            preferencesViewModel = preferencesViewModel,
            onDismiss = {
                showSelectDateDialog.value = false
            },
            onDateRangeSelected = { dateRange ->
                showSelectDateDialog.value = false
                startDateTime.value = dateRange.first
                endDateTime.value = dateRange.second

                selectedTimePeriod = if (dateRange.first == getStartOfTheDay(1, 1, 1970) && dateRange.second == todayTimeStamp) {
                    "All time"
                } else {
                    "${dateFormat.format(dateRange.first)} - ${dateFormat.format(dateRange.second)}"
                }
            }
        )
    }
}

@Composable
fun StatsPage1(
    preferencesViewModel: PreferencesViewModel,
    endDateTime: MutableState<Long?>,
    startDateTime: MutableState<Long?>
){
    val pagerState = rememberPagerState(pageCount = {4})

    val context = LocalContext.current
    val database = remember { DatabaseProvider.getDatabase(context) }

    val unitStore by preferencesViewModel.unitFlow.collectAsState()
    val defaultUnit by remember {mutableStateOf(unitStore) }

    val calendar = Calendar.getInstance()

    calendar.add(Calendar.DAY_OF_YEAR, -6)

    val allExercises = remember { mutableStateListOf<ExerciseEntity>() }
    val workoutEntities = remember { mutableStateListOf<WorkoutEntity>() }
    val setEntities = remember { mutableStateListOf<List<SetEntity>>() }
    val workoutExercises = remember { mutableStateListOf<WorkoutExerciseEntity>() }

    val muscleGroups = listOf("Whole Body", "Legs", "Back", "Chest", "Shoulders", "Arms", "Core", "Forearms")
    val categoryGroups = listOf("Barbell", "Bodyweight", "Dumbbell", "Machine", "Cable")

    val muscleSets = remember { muscleGroups.associateWith { mutableListOf<SetEntity>()}}
    val categorySets = remember { categoryGroups.associateWith { mutableListOf<SetEntity>()}}
    val muscleVolumes = remember { muscleGroups.associateWith { mutableDoubleStateOf(0.0) }}
    val categoryVolumes = remember { categoryGroups.associateWith { mutableDoubleStateOf(0.0) }}

    val df = DecimalFormat("#.##")

    val selectedBodyPart = remember { mutableStateOf("") }
    val selectedCategory = remember { mutableStateOf("") }
    val exerciseVolumeList = remember { mutableStateListOf<VolumeData>() }

    val isWeight = remember { mutableStateOf(true) }
    val statsItemDataListsList = remember {mutableStateListOf<List<StatsItemData>>()}

    LaunchedEffect(startDateTime.value, endDateTime.value) {
        pagerState.scrollToPage(0)
        exerciseVolumeList.clear()
        selectedBodyPart.value = ""
        selectedCategory.value = ""

        allExercises.clear()
        allExercises.addAll(database.exerciseDao().getAllExercises())

        workoutEntities.clear()
        workoutEntities.addAll(database.workoutDao().getAllWorkoutsBetweenTimestamps(startDateTime.value!!, endDateTime.value!!))

        setEntities.clear()
        setEntities.addAll(workoutEntities.map { workout ->
            database.setDao().getSetsByWorkoutId(workout.id)
        })
        workoutExercises.clear()
        workoutExercises.addAll(database.workoutDao().getAllWorkoutExercisesBetweenTimestamps(startDateTime.value!!, endDateTime.value!!))

        muscleSets.forEach { (_, sets) -> sets.clear() }
        categorySets.forEach { (_, sets) -> sets.clear() }

        setEntities.forEach { setList ->
            setList.forEach { set ->
                val bodyPart = allExercises.find { it.id == set.exerciseId }?.bodyPart
                when (bodyPart) {
                    "Biceps", "Triceps" -> muscleSets["Arms"]?.add(set)
                    else -> bodyPart?.let {
                        muscleSets[bodyPart]?.add(set)
                    }
                }
            }
        }

        setEntities.forEach { setList ->
            setList.forEach { set ->
                val category = allExercises.find { it.id == set.exerciseId }?.category
                categorySets[category]?.add(set)
            }
        }

        muscleVolumes.forEach { (_, volume) -> volume.doubleValue = 0.0 }
        categoryVolumes.forEach { (_, volume) -> volume.doubleValue = 0.0 }

        muscleSets.forEach { (group, sets) ->
            sets.forEach { set ->
                val weight = set.weight
                val convertedWeight = convertWeight(weight, set.unit, defaultUnit)
                if (set.weight.isNotEmpty() && set.reps.isNotEmpty()) {
                    muscleVolumes[group]?.doubleValue =
                        muscleVolumes[group]?.doubleValue?.plus(df.format(convertedWeight).toDouble() * set.reps.toDouble())!!
                }
            }
        }

        categorySets.forEach { (group, sets) ->
            sets.forEach { set ->
                val weight = set.weight
                val convertedWeight = convertWeight(weight, set.unit, defaultUnit)
                if (set.weight.isNotEmpty() && set.reps.isNotEmpty()) {
                    categoryVolumes[group]?.doubleValue =
                        categoryVolumes[group]?.doubleValue?.plus(df.format(convertedWeight).toDouble() * set.reps.toDouble())!!
                }

            }
        }

        val totalTime = mutableLongStateOf(workoutEntities.sumOf { it.duration })
        val avgTime = mutableLongStateOf(
            if (workoutEntities.isEmpty()) 0L
            else totalTime.longValue / workoutEntities.size
        )
        var maxTimeDate: Long? = null
        var minTimeDate: Long? = null
        var maxTime = 0L
        var minTime = 0L
        workoutEntities.forEach { workout ->
            if(maxTime == 0L){
                maxTime = workout.duration
                maxTimeDate = workout.startTime
                minTime = workout.duration
                minTimeDate = workout.startTime
            }else if (workout.duration < minTime) {
                minTime = workout.duration
                minTimeDate = workout.startTime
            }else if (workout.duration > maxTime) {
                maxTime = workout.duration
                maxTimeDate = workout.startTime
            }
        }

        val totalVolume = mutableDoubleStateOf(muscleVolumes.values.sumOf { it.doubleValue })
        val roundedTotalVolume = Math.round(totalVolume.doubleValue).toInt()
        val avgVolume = mutableDoubleStateOf(
            if (workoutEntities.isEmpty()) 0.0
            else totalVolume.doubleValue / workoutEntities.size)
        val roundedAvgVolume = Math.round(avgVolume.doubleValue).toInt()
        var maxVolumeDate: Long? = null
        var minVolumeDate: Long? = null
        var maxVolume = 0.0
        var minVolume = 0.0
        workoutEntities.forEach { workout ->
            val workoutExercisesForWorkout = workoutExercises.filter { it.workoutId == workout.id }
            val setsForWorkout = setEntities.flatten().filter { set ->
                workoutExercisesForWorkout.any {
                    it.exerciseId == set.exerciseId && it.workoutId == set.workoutId
                }
            }

            val volume = setsForWorkout.sumOf { set ->
                if (set.weight.isNotEmpty() && set.reps.isNotEmpty()) {
                    val convertedWeight = convertWeight(set.weight, set.unit, defaultUnit)
                    df.format(convertedWeight).toDouble() * set.reps.toDouble()
                } else 0.0
            }

            if(maxVolume == 0.0){
                maxVolume = volume
                maxVolumeDate = workout.startTime
                minVolume = volume
                minVolumeDate = workout.startTime
            }else if (volume < minVolume) {
                minVolume = volume
                minVolumeDate = workout.startTime
            }else if (volume > maxVolume) {
                maxVolume = volume
                maxVolumeDate = workout.startTime
            }
        }
        val roundedMaxVolume = Math.round(maxVolume).toInt()
        val roundedMinVolume = Math.round(minVolume).toInt()

        val totalExercises = mutableIntStateOf(workoutExercises.size)
        val avgExercises = mutableIntStateOf(
            if (workoutEntities.isEmpty()) 0
            else totalExercises.intValue / workoutEntities.size)
        var maxExercisesDate: Long? = null
        var minExercisesDate: Long? = null
        val maxExercises = mutableIntStateOf(0)
        val minExercises = mutableIntStateOf(0)
        workoutEntities.maxOfOrNull { workout ->
            workoutExercises.count { it.workoutId == workout.id }
        }
        workoutEntities.forEach { workout ->
            val numberOfExercises = workoutExercises.count { it.workoutId == workout.id }
            if (maxExercises.intValue == 0) {
                maxExercises.intValue = numberOfExercises
                maxExercisesDate = workout.startTime
                minExercises.intValue = numberOfExercises
                minExercisesDate = workout.startTime
            } else if (numberOfExercises < minExercises.intValue) {
                minExercises.intValue = numberOfExercises
                minExercisesDate = workout.startTime
            } else if (numberOfExercises > maxExercises.intValue) {
                maxExercises.intValue = numberOfExercises
                maxExercisesDate = workout.startTime
            }
        }

        val totalSets = mutableIntStateOf(setEntities.sumOf { it.size })
        val avgSets = mutableIntStateOf(
            if (workoutEntities.isEmpty()) 0
            else totalSets.intValue / workoutEntities.size)
        var maxSetsDate: Long? = null
        var minSetsDate: Long? = null
        val maxSets = mutableIntStateOf(0)
        val minSets = mutableIntStateOf(0)
        setEntities.maxByOrNull { it.size }?.size ?: 0
        setEntities.forEach { setList ->
            val numberOfSets = setList.size
            if (maxSets.intValue == 0) {
                maxSets.intValue = numberOfSets
                maxSetsDate = workoutEntities[setEntities.indexOf(setList)].startTime
                minSets.intValue = numberOfSets
                minSetsDate = workoutEntities[setEntities.indexOf(setList)].startTime
            }else if (numberOfSets < minSets.intValue) {
                minSets.intValue = numberOfSets
                minSetsDate = workoutEntities[setEntities.indexOf(setList)].startTime
            }else if (numberOfSets > maxSets.intValue) {
                maxSets.intValue = numberOfSets
                maxSetsDate = workoutEntities[setEntities.indexOf(setList)].startTime
            }
        }

        val totalReps = mutableIntStateOf(setEntities.sumOf { setList ->
            setList.sumOf { it.reps.toIntOrNull() ?: 0 }
        })
        val avgReps = mutableIntStateOf(
            if (workoutEntities.isEmpty()) 0
            else totalReps.intValue / workoutEntities.size)
        var maxRepsDate: Long? = null
        var minRepsDate: Long? = null
        val maxReps = mutableIntStateOf(0)
        val minReps = mutableIntStateOf(0)
        setEntities.maxByOrNull { setList ->
            setList.sumOf { it.reps.toIntOrNull()?:0 }
        }?.sumOf { it.reps.toIntOrNull() ?: 0 } ?: 0
        setEntities.forEach { setList ->
            val numberOfReps = setList.sumOf { it.reps.toIntOrNull()?:0 }
            if (maxReps.intValue == 0) {
                maxReps.intValue = numberOfReps
                maxRepsDate = workoutEntities[setEntities.indexOf(setList)].startTime
                minReps.intValue = numberOfReps
                minRepsDate = workoutEntities[setEntities.indexOf(setList)].startTime
            }else if (numberOfReps < minReps.intValue) {
                minReps.intValue = numberOfReps
                minRepsDate = workoutEntities[setEntities.indexOf(setList)].startTime
            }else if (numberOfReps > maxReps.intValue) {
                maxReps.intValue = numberOfReps
                maxRepsDate = workoutEntities[setEntities.indexOf(setList)].startTime
            }
        }
        statsItemDataListsList.clear()
        statsItemDataListsList.addAll(fillStatsItemDataListsList(
            totalTime = totalTime.longValue,
            avgTime = avgTime.longValue,
            maxTime = maxTime,
            minTime = minTime,
            maxTimeDate = maxTimeDate,
            minTimeDate = minTimeDate,
            totalVolume = roundedTotalVolume,
            avgVolume = roundedAvgVolume,
            maxVolume = roundedMaxVolume,
            minVolume = roundedMinVolume,
            maxVolumeDate = maxVolumeDate,
            minVolumeDate = minVolumeDate,
            totalExercises = totalExercises.intValue,
            avgExercises = avgExercises.intValue,
            maxExercises = maxExercises.intValue,
            minExercises = minExercises.intValue,
            maxExercisesDate = maxExercisesDate,
            minExercisesDate = minExercisesDate,
            totalSets = totalSets.intValue,
            avgSets = avgSets.intValue,
            maxSets = maxSets.intValue,
            minSets = minSets.intValue,
            maxSetsDate = maxSetsDate,
            minSetsDate = minSetsDate,
            totalReps = totalReps.intValue,
            avgReps = avgReps.intValue,
            maxReps = maxReps.intValue,
            minReps = minReps.intValue,
            maxRepsDate = maxRepsDate,
            minRepsDate = minRepsDate,
            unit = defaultUnit
        ))
        delay(250)
    }

    LaunchedEffect(selectedBodyPart.value) {
        exerciseVolumeList.clear()
        var setsForSelectedBodyPart = muscleSets[selectedBodyPart.value]
        if (setsForSelectedBodyPart == null) {
            setsForSelectedBodyPart = categorySets[selectedBodyPart.value]
        }
        val groupedByExercise = setsForSelectedBodyPart?.groupBy { it.exerciseId }

        groupedByExercise?.forEach { (exerciseId, sets) ->
            val totalVolume = sets.sumOf { set ->
                val repsDouble = if(set.reps.isEmpty()) 0.0 else set.reps.toDouble()
                convertWeight(set.weight, set.unit, defaultUnit) * repsDouble
            }
            exerciseVolumeList.add(
                VolumeData(
                    exerciseName = allExercises.find { it.id == exerciseId }?.name ?: "",
                    totalVolume = totalVolume,
                    totalSets = sets.size
                )
            )
        }

    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ){
        val pieChartLabel = remember { mutableStateOf("") }
        when (pagerState.currentPage) {
            0 -> {
                pieChartLabel.value = "Weight per body part"
            }
            1 -> {
                pieChartLabel.value = "Sets per body part"
            }
            2 -> {
                pieChartLabel.value = "Weight per category"
            }
            else -> {
                pieChartLabel.value = "Sets per category"
            }
        }
        Text(
            modifier = Modifier
                .padding(top = 8.dp),
            text = pieChartLabel.value
        )
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .padding(horizontal = 16.dp)
                .padding(vertical = 8.dp),
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                disabledContentColor = Color.Transparent,
            ),
            shape = RoundedCornerShape(50.dp)
        ){
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ){
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly
                ){
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(0.9f)
                    ){
                        HorizontalPager(
                            modifier = Modifier
                                .fillMaxWidth(),
                            state = pagerState,
                        ){ page ->
                            when(page){
                                0 -> PieChartView(
                                    entries =
                                    muscleVolumes.map {PieEntry(it.value.doubleValue.toFloat(), it.key) },
                                    isWeight = true,
                                    preferencesViewModel = preferencesViewModel,
                                    onValueSelected = {
                                        selectedBodyPart.value = it.label
                                        isWeight.value = true
                                    },
                                    onNothingSelected = {selectedBodyPart.value = ""}
                                )
                                1 -> PieChartView(
                                    entries =
                                    muscleSets.map { PieEntry(it.value.size.toFloat(), it.key) },
                                    isWeight = false,
                                    preferencesViewModel = preferencesViewModel,
                                    onValueSelected = {
                                        selectedBodyPart.value = it.label
                                        isWeight.value = false
                                    },
                                    onNothingSelected = {selectedBodyPart.value = ""}
                                )
                                2 -> {
                                    PieChartView(
                                        entries =
                                        categoryVolumes.map {PieEntry(it.value.doubleValue.toFloat(), it.key) },
                                        isWeight = true,
                                        preferencesViewModel = preferencesViewModel,
                                        onValueSelected = {
                                            selectedBodyPart.value = it.label
                                            isWeight.value = true
                                        },
                                        onNothingSelected = {selectedBodyPart.value = ""}
                                    )
                                }
                                3 -> {
                                    PieChartView(
                                        entries =
                                        categorySets.map { PieEntry(it.value.size.toFloat(), it.key) },
                                        isWeight = false,
                                        preferencesViewModel = preferencesViewModel,
                                        onValueSelected = {
                                            selectedBodyPart.value = it.label
                                            isWeight.value = false
                                        },
                                        onNothingSelected = {selectedBodyPart.value = ""}
                                    )
                                }

                            }
                        }
                    }

                    HorizontalPagerIndicator(
                        pagerState = pagerState,
                        pageCount = 4,
                        activeColor = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }


        VolumeItem(
            volumeDataList = exerciseVolumeList,
            bodyPart = selectedBodyPart.value,
            isWeight = isWeight.value,
            unit = defaultUnit
        )

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .padding(horizontal = 16.dp),
        ) {
            items(statsItemDataListsList.size) { index ->
                StatsItem(
                    modifier = Modifier
                        .padding(horizontal = 6.dp),
                    statsItemDataList = statsItemDataListsList[index]
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))

    }
}

@Composable
fun StatsPage2(
    preferencesViewModel: PreferencesViewModel,
    endDateTime: MutableState<Long?>,
    startDateTime: MutableState<Long?>
){
    val themeStore by preferencesViewModel.themeFlow.collectAsState()
    val unitStore by preferencesViewModel.unitFlow.collectAsState()
    val theme by remember { mutableStateOf(themeStore) }
    val defaultUnit by remember {mutableStateOf(unitStore) }

    val showExerciseSelector = remember { mutableStateOf(false) }
    val selectedExercise = preferencesViewModel.statsExercise
    val listForSpinner = remember { mutableStateListOf<String>()}
    val selectedType = rememberSaveable { mutableStateOf("1RM") }
    val exercises = remember { mutableStateListOf<Exercise>()}
    val workoutExercises = remember { mutableStateListOf<WorkoutExerciseEntity>()}
    val workoutEntities = remember { mutableStateListOf<WorkoutEntity>()}
    val context = LocalContext.current
    val setEntities = remember { mutableStateListOf<List<SetEntity>>() }
    val setsByStartTime = remember { mutableStateMapOf<Long, List<SetEntity>>() }
    val database = remember{ DatabaseProvider.getDatabase(context) }
    val firstChartText = remember { mutableStateOf("Choose an exercise") }
    val secondChartText = remember { mutableStateOf("Choose an exercise") }
    val entriesForFirstChart = remember { mutableStateListOf<Entry>() }
    val entriesForSecondChart = remember { mutableStateListOf<Entry>() }
    val entriesForSecondChart2 = remember { mutableStateListOf<Entry>() }

    val type = remember { mutableStateOf("") }
    val baseTime = remember { mutableLongStateOf(0L) }
    val exerciseDataList = remember { mutableStateListOf<ExerciseData>() }

    fun calculate1RM(){
        type.value = "1RM"
        entriesForFirstChart.clear()
        entriesForSecondChart.clear()
        entriesForSecondChart2.clear()
        exerciseDataList.clear()

        baseTime.longValue = setsByStartTime.keys.minOrNull() ?: 0L
        setsByStartTime.keys.sorted().forEach { startTime ->
            val day = getDaysForChart(baseTime.longValue, startTime)
            val sets = setsByStartTime[startTime]
            var estimated1RM = 0.0
            var weight2 = 0.0
            var reps2 = 0
            for (set in sets!!) {
                val weight = convertWeight(set.weight, set.unit, defaultUnit)
                val reps = set.reps.toIntOrNull() ?: 0
                if (reps > 0){
                    if(reps > 1){
                        val epley = weight * (1 + reps / 30f)
                        val brzycki = weight * (36/(37f-reps))
                        val adams = weight * (1/(1-0.02*reps))
                        val baechle = weight * (1 + 0.033*reps)
                        val berger = weight * (1/(1.0261* exp(-0.0262*reps)))
                        val brown = weight * (0.98489 + 0.0328*reps)
                        val kemmler = weight * (0.988 + 0.0104*reps + 0.00190 * reps * reps - 0.0000584 * reps * reps * reps)
                        val landers = weight * (1/(1.013 - 0.0267123*reps))
                        val lombardi = weight * reps.toDouble().pow(0.1)
                        val mayhew = weight * (1/(0.522+0.419* exp(-0.055*reps)))
                        val naclerio = weight * (1/(0.951* exp(-0.021*reps)))
                        val oconner = weight * (1 + 0.025*reps)
                        val wathen = weight * (1/(0.4880 + 0.538 * exp(-0.075*reps)))
                        val sumEstimated1RM  = (epley + brzycki + adams + baechle + berger + brown + kemmler + landers + lombardi + mayhew + naclerio + oconner + wathen).toDouble()
                        val estimated1RMOfSet = sumEstimated1RM / 13
                        if (estimated1RMOfSet > estimated1RM) {
                            estimated1RM = estimated1RMOfSet
                            weight2 = weight
                            reps2 = reps
                        }
                    }else{
                        if (weight > estimated1RM) {
                            estimated1RM = weight
                            weight2 = weight
                            reps2 = reps
                        }
                    }
                }
            }
            if (estimated1RM > 0.0) {
                entriesForFirstChart.add(Entry(day, estimated1RM.toFloat()))
                entriesForSecondChart.add(Entry(day, weight2.toFloat()))
                entriesForSecondChart2.add(Entry(day, reps2.toFloat()))
            }
        }
    }

    fun calculateMaxWeight(){
        type.value = "Max"
        entriesForFirstChart.clear()
        entriesForSecondChart.clear()
        entriesForSecondChart2.clear()
        baseTime.longValue = setsByStartTime.keys.minOrNull() ?: 0L
        setsByStartTime.keys.sorted().forEach { startTime ->
            val day = getDaysForChart(baseTime.longValue, startTime)
            val sets = setsByStartTime[startTime]
            val maxWeight = sets?.maxOfOrNull { convertWeight(it.weight, it.unit, defaultUnit) } ?: 0.0
            var maxReps = 0
            for(set in sets!!){
                if((convertWeight(set.weight, set.unit, defaultUnit)) == maxWeight){
                    maxReps += set.reps.toIntOrNull() ?: 0
                }
            }
            if (maxReps > 0) {
                entriesForFirstChart.add(Entry(day, maxWeight.toFloat()))
                entriesForSecondChart2.add(Entry(day, maxReps.toFloat()))
            }

        }
    }

    fun calculateAverage(){
        type.value = "Avg"
        entriesForFirstChart.clear()
        entriesForSecondChart.clear()
        entriesForSecondChart2.clear()
        baseTime.longValue = setsByStartTime.keys.minOrNull() ?: 0L
        setsByStartTime.keys.sorted().forEach { startTime ->
            val day = getDaysForChart(baseTime.longValue, startTime)
            val sets = setsByStartTime[startTime]
            val weightSum = sets?.sumOf { (convertWeight(it.weight, it.unit, defaultUnit).times(it.reps.toDoubleOrNull() ?: 0.0))} ?: 0.0
            val repsSum = sets?.sumOf { it.reps.toIntOrNull() ?: 0 } ?: 0
            val avgReps = (repsSum.toDouble() / sets?.size!!)
            if (repsSum > 0) {
                val avgWeight = weightSum / repsSum
                entriesForFirstChart.add(Entry(day, avgWeight.toFloat()))
                entriesForSecondChart2.add(Entry(day, avgReps.toFloat()))

            }
        }
    }

    fun calculateSets(){
        entriesForFirstChart.clear()
        entriesForSecondChart.clear()
        entriesForSecondChart2.clear()
        baseTime.longValue = setsByStartTime.keys.minOrNull() ?: 0L
        val number = selectedType.value.split(" ")[0].toInt()
        setsByStartTime.keys.sorted().forEach { startTime ->
            val day = getDaysForChart(baseTime.longValue, startTime)
            val sets = setsByStartTime[startTime]
            val set = sets?.getOrNull(number - 1)
            if (set != null) {
                val weight = convertWeight(set.weight, set.unit, defaultUnit)
                val reps = set.reps.toIntOrNull() ?: 0
                if(reps > 0){
                    entriesForFirstChart.add(Entry(day, weight.toFloat()))
                    entriesForSecondChart2.add(Entry(day, set.reps.toFloat()))
                }

            }
        }
    }
    LaunchedEffect(selectedExercise.value, startDateTime.value, endDateTime.value) {
        exercises.clear()
        exercises.addAll(ExerciseRepository.getExercisesMapped(context))

        workoutEntities.clear()
        workoutEntities.addAll(
            database.workoutDao()
                .getAllWorkoutsBetweenTimestamps(startDateTime.value!!, endDateTime.value!!)
        )

        workoutExercises.clear()
        workoutExercises.addAll(
            database.workoutDao()
                .getAllWorkoutExercisesBetweenTimestamps(startDateTime.value!!, endDateTime.value!!)
        )

        exercises.retainAll { exercise ->
            workoutExercises.any { it.exerciseId.toString() == exercise.id }
        }

        if (selectedExercise.value != null) {
            if (!exercises.contains(selectedExercise.value)) {
                preferencesViewModel.statsExercise.value = null
            }
        }
        setEntities.clear()

        val matchingSets = workoutExercises
            .filter { workoutExercise ->
                workoutExercise.exerciseId.toString() == selectedExercise.value?.id
            }.sortedBy { it.workoutId }
            .map { workoutExercise ->
                database.setDao().getSetsForExercise(
                    workoutExercise.workoutId,
                    workoutExercise.exerciseId
                )
            }
        setEntities.addAll(matchingSets)

        setsByStartTime.clear()
        workoutEntities.sortedBy { it.id }.forEach { workout ->
            val setsForWorkout = setEntities.find { it.isNotEmpty() && it[0].workoutId == workout.id }
            if (!setsForWorkout.isNullOrEmpty()) {
                setsByStartTime[workout.startTime] = setsForWorkout
            }
        }

        if(exercises.isNotEmpty() && selectedExercise.value == null){
            preferencesViewModel.statsExercise.value = exercises[0]
        }


        listForSpinner.clear()
        listForSpinner.addAll(listOf("1RM", "Max weight", "Average"))
        val maxSetNumber = setEntities.maxOfOrNull { it.size } ?: 0
        for (i in 1..maxSetNumber) {
            listForSpinner.add("$i set")
        }
        if(!listForSpinner.contains(selectedType.value)){
            if(selectedExercise.value == null){
                selectedType.value = "1RM"
            }else{
                selectedType.value = listForSpinner.last()
            }
        }

        when(selectedType.value){
            "1RM" -> {
                calculate1RM()
            }
            "Max weight" -> {
                calculateMaxWeight()
            }
            "Average" -> {
                calculateAverage()
            }
            else -> {
                calculateSets()
            }
        }
        exerciseDataList.clear()
        val highest = entriesForFirstChart.maxOfOrNull { it.y } ?: 0f
        val roundedHighest = String.format("%.2f", highest)
        val lowest = entriesForFirstChart.minOfOrNull { it.y } ?: 0f
        val roundedLowest = String.format("%.2f", lowest)
        val workouts = setsByStartTime.size
        val sets = setsByStartTime.values.sumOf { it.size }
        val reps = setsByStartTime.values.sumOf { it.sumOf { set -> set.reps.toIntOrNull() ?: 0 } }
        val type2 = when(selectedType.value){
            "1RM" -> "1RM"
            "Max weight" -> "maximal weight"
            "Average" -> "average weight"
            else -> "weight"
        }
        exerciseDataList.addAll(
            listOf(
                ExerciseData(
                    firstPart = "Highest $type2",
                    secondPart = "$roundedHighest $defaultUnit"
                ),
                ExerciseData(
                    firstPart = "Lowest $type2",
                    secondPart = "$roundedLowest $defaultUnit"
                ),
                ExerciseData(
                    firstPart = "Number of workouts",
                    secondPart = "$workouts"
                ),
                ExerciseData(
                    firstPart = "Number of sets",
                    secondPart = "$sets"
                ),
                ExerciseData(
                    firstPart = "Number of reps",
                    secondPart = "$reps"
                )
            )
        )
    }

    LaunchedEffect(selectedType.value) {
        when(selectedType.value){
            "1RM" -> {
                calculate1RM()
            }
            "Max weight" -> {
                calculateMaxWeight()
            }
            "Average" -> {
                calculateAverage()
            }
            else -> {
                calculateSets()
            }
        }
        exerciseDataList.clear()
        val highest = entriesForFirstChart.maxOfOrNull { it.y } ?: 0f
        val roundedHighest = String.format("%.2f", highest)
        val lowest = entriesForFirstChart.minOfOrNull { it.y } ?: 0f
        val roundedLowest = String.format("%.2f", lowest)
        val workouts = setsByStartTime.size
        val sets = setsByStartTime.values.sumOf { it.size }
        val reps = setsByStartTime.values.sumOf { it.sumOf { set -> set.reps.toIntOrNull() ?: 0 } }
        val type2 = when(selectedType.value){
            "1RM" -> "1RM"
            "Max weight" -> "maximal weight"
            "Average" -> "average weight"
            else -> "weight"
        }
        exerciseDataList.addAll(
            listOf(
                ExerciseData(
                    firstPart = "Highest $type2",
                    secondPart = "$roundedHighest $defaultUnit"
                ),
                ExerciseData(
                    firstPart = "Lowest $type2",
                    secondPart = "$roundedLowest $defaultUnit"
                ),
                ExerciseData(
                    firstPart = "Number of workouts",
                    secondPart = "$workouts"
                ),
                ExerciseData(
                    firstPart = "Number of sets",
                    secondPart = "$sets"
                ),
                ExerciseData(
                    firstPart = "Number of reps",
                    secondPart = "$reps"
                )
            )
        )
    }

    if(selectedExercise.value != null){
        when (selectedType.value) {
            "1RM" -> {
                firstChartText.value = "Estimated 1RM"
                secondChartText.value = "Corresponding sets"
            }
            "Max weight" -> {
                firstChartText.value = "Maximal weight"
                secondChartText.value = "Reps with maximal weight"
            }
            "Average" -> {
                firstChartText.value = "Average weight"
                secondChartText.value = "Average reps"
            }
            else -> {
                firstChartText.value = "Weight"
                secondChartText.value = "Sets"
            }
        }
    }else{
        firstChartText.value = "Choose an exercise"
        secondChartText.value = "Choose an exercise"
    }

    if(showExerciseSelector.value){
        ExerciseSelector(
            exercises = exercises,
            onDismiss = {showExerciseSelector.value = false},
            onExerciseSelected = {
                preferencesViewModel.statsExercise.value = it
                showExerciseSelector.value = false
            }
        )
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically

        ){
            Button(
                modifier = Modifier
                    .weight(2f)
                    .height(40.dp)
                    .padding(end = 4.dp),
                onClick = {
                    showExerciseSelector.value = true
                },
                shape = RoundedCornerShape(16.dp)
            ){
                Text(text = if(selectedExercise.value != null) selectedExercise.value!!.name else "Select Exercise")
            }
            Spinner(
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .padding(start = 4.dp),
                items = listForSpinner,
                selectedItem = selectedType.value,
                onItemSelected = {
                    selectedType.value = it
                }
            )
        }
        Text(
            text = firstChartText.value,
            fontSize = MaterialTheme.typography.titleMedium.fontSize,
        )
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2f)
                .padding(horizontal = 16.dp)
                .padding(vertical = 8.dp),
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                disabledContentColor = Color.Transparent,
            ),
            shape = RoundedCornerShape(10.dp)
        ){
            LineChartView(
                baseTime = baseTime.longValue,
                firstEntries = entriesForFirstChart,
                type = type.value,
                unit = defaultUnit,
                theme = theme
            )
        }
        Text(
            text = secondChartText.value,
            fontSize = MaterialTheme.typography.titleMedium.fontSize,

        )
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2f)
                .padding(horizontal = 16.dp)
                .padding(vertical = 8.dp),
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                disabledContentColor = Color.Transparent,
            ),
            shape = RoundedCornerShape(10.dp)
        ){
            LineChartView(
                baseTime = baseTime.longValue,
                firstEntries = entriesForSecondChart,
                secondEntries = entriesForSecondChart2,
                type = type.value,
                unit = defaultUnit,
                theme = theme
            )
        }
        ExerciseItem(
            exerciseDataList = exerciseDataList,
            label = selectedExercise.value?.name ?: ""
        )
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun StatsScreenPreview(){
    val endDateTime = remember{ mutableStateOf<Long?>(null)}
    val startDateTime = remember{ mutableStateOf<Long?>(null)}

    Workout_App_2Theme {
       // StatsPage2(preferencesViewModel = PreferencesViewModel(PreferencesDataStore(LocalContext.current)), endDateTime = endDateTime, startDateTime = startDateTime)
    }
}