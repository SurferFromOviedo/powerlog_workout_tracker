package com.example.workout_app_2.presentation

import android.graphics.Color
import android.view.ViewGroup
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.example.workout_app_2.ui.theme.Workout_App_2Theme
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

@Composable
fun LineChartView(
    baseTime: Long,
    firstEntries:List<Entry>,
    secondEntries: List<Entry> = emptyList(),
    unit: String,
    theme: String,
    type: String
){

    fun adjustEntriesWithSameX(entries: List<Entry>): List<Entry> {
        val adjustedEntries = mutableListOf<Entry>()
        val xValueCounts = mutableMapOf<Float, Int>()

        entries.forEach { entry ->
            val x = entry.x
            val y = entry.y
            val count = xValueCounts.getOrDefault(x, 0)
            val adjustedX = if (count > 0) x + 0.0001f * count else x
            xValueCounts[x] = count + 1
            adjustedEntries.add(Entry(adjustedX, y))
        }

        return adjustedEntries
    }

    val adjustedFirstEntries = adjustEntriesWithSameX(firstEntries)
    val adjustedSecondEntries = adjustEntriesWithSameX(secondEntries)

    val utcTimeZone = TimeZone.getTimeZone("UTC")

    val baseDate = Calendar.getInstance(utcTimeZone).apply {
        timeInMillis = baseTime

        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    val baseDateInMillis = baseDate.timeInMillis

    val darkTheme = when (theme) {
        "Dark" -> true
        "Light" -> false
        else -> isSystemInDarkTheme()
    }
    val colorText = if(darkTheme) Color.WHITE else Color.BLACK

    val yAxisLabel: String
    val yAxisLabel2: String

    when(type){
        "1RM" -> {
            yAxisLabel = "Weight, $unit"
            yAxisLabel2 = "Reps"

        }
        "Max" -> {
            yAxisLabel = "Weight, $unit"
            yAxisLabel2 = "Reps"
        }
        "Avg" -> {
            yAxisLabel = "Weight, $unit"
            yAxisLabel2 = "Reps"
        }
        else -> {
            yAxisLabel = "Weight, $unit"
            yAxisLabel2 = "Reps"
        }
    }

    val customFormatter = object: ValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            return BigDecimal(value.toDouble()).setScale(2, RoundingMode.HALF_UP).toString()
        }

    }

    AndroidView(
        factory = { context ->
            LineChart(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                description.isEnabled = false
                setTouchEnabled(true)
                setDragEnabled(true)
                setScaleEnabled(true)
                setPinchZoom(true)
                setDrawGridBackground(false)

                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(false)
                    textColor = colorText
                    axisLineColor = Color.GRAY
                    valueFormatter = object : ValueFormatter(){
                        private val dateFormat = SimpleDateFormat("dd.MM", Locale.getDefault()).apply {
                            timeZone = TimeZone.getTimeZone("UTC")
                        }
                        override fun getFormattedValue(value: Float): String {
                            val formattedDateInMillis = baseDateInMillis + (value * 24 * 60 * 60 * 1000).toLong()
                            return dateFormat.format(formattedDateInMillis)
                        }
                    }
                }
                axisLeft.apply {
                    textColor = colorText
                    axisLineColor = Color.GRAY
                    setDrawGridLines(true)
                }

                axisRight.apply {
                    isEnabled = false
                    textColor = colorText
                    axisLineColor = Color.GRAY
                    setDrawGridLines(true)
                }
                legend.isEnabled = true
                legend.textColor = colorText

                val lineDataSet = LineDataSet(adjustedFirstEntries, yAxisLabel).apply {
                    color = Color.BLUE
                    lineWidth = 2f
                    setDrawCircles(true)
                    setCircleColor(Color.BLUE)
                    setDrawValues(true)
                    valueTextColor = colorText
                    valueTextSize = 10f
                    setDrawFilled(false)
                    valueFormatter = customFormatter
                }

                val lineDataSet2 = LineDataSet(adjustedSecondEntries, yAxisLabel2).apply {
                    color = Color.RED
                    setDrawCircles(true)
                    setCircleColor(Color.RED)
                    setDrawValues(true)
                    valueTextColor = colorText
                    valueTextSize = 10f
                    setDrawFilled(false)
                    valueFormatter = customFormatter
                }
                data = LineData(lineDataSet, lineDataSet2)
                invalidate()
            }
        },
        update = { chart ->
            chart.apply {
                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(false)
                    textColor = colorText
                    axisLineColor = Color.GRAY
                    valueFormatter = object : ValueFormatter(){
                        private val dateFormat = SimpleDateFormat("dd.MM", Locale.getDefault()).apply {
                            timeZone = TimeZone.getTimeZone("UTC")
                        }
                        override fun getFormattedValue(value: Float): String {
                            val formattedDateInMillis = baseDateInMillis + (value * 24 * 60 * 60 * 1000).toLong()
                            return dateFormat.format(formattedDateInMillis)
                        }
                    }
                }

            }
            if(firstEntries.isNotEmpty() || secondEntries.isNotEmpty()){
                val lineDataSet = LineDataSet(adjustedFirstEntries, yAxisLabel).apply {
                    color = Color.BLUE
                    lineWidth = 2f
                    setDrawCircles(true)
                    setCircleColor(Color.BLUE)
                    setDrawValues(true)
                    valueTextColor = colorText
                    valueTextSize = 10f
                    setDrawFilled(false)
                    valueFormatter = customFormatter
                }

                val lineDataSet2 = LineDataSet(adjustedSecondEntries, yAxisLabel2).apply {
                    color = Color.RED
                    lineWidth = 2f
                    setDrawCircles(true)
                    setCircleColor(Color.RED)
                    setDrawValues(true)
                    valueTextColor = colorText
                    valueTextSize = 10f
                    setDrawFilled(false)
                    valueFormatter = customFormatter
                }

                chart.data = LineData(lineDataSet, lineDataSet2)
                chart.setMaxVisibleValueCount(if (secondEntries.isNotEmpty()) 30 else 15)
                chart.notifyDataSetChanged()
                chart.invalidate()
            }else{
                chart.clear()
            }
        }
    )
}

@Preview
@Composable
fun LineChartViewPreview(){
    Workout_App_2Theme {
        val entries = listOf(
            Entry(0f, 99.7f),
            Entry(1f, 50f),
            Entry(1.00001f, 100f),


        )
        LineChartView(baseTime = 0L, firstEntries = entries, type = "Weight", unit = "KG", theme = "Light")
    }
}
