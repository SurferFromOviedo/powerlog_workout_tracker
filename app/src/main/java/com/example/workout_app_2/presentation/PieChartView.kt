package com.example.workout_app_2.presentation

import android.view.ViewGroup
import androidx.compose.animation.core.animate
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.example.workout_app_2.data.PreferencesDataStore
import com.example.workout_app_2.data.PreferencesViewModel
import com.example.workout_app_2.ui.theme.BlueIndicator
import com.example.workout_app_2.ui.theme.GreenIndicator
import com.example.workout_app_2.ui.theme.OrangeIndicator
import com.example.workout_app_2.ui.theme.PinkIndicator
import com.example.workout_app_2.ui.theme.PurpleIndicator
import com.example.workout_app_2.ui.theme.RedIndicator
import com.example.workout_app_2.ui.theme.Workout_App_2Theme
import com.example.workout_app_2.ui.theme.YellowIndicator
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener


@Composable
fun PieChartView(
    entries: List<PieEntry>,
    isWeight: Boolean,
    preferencesViewModel: PreferencesViewModel,
    onValueSelected: (PieEntry) -> Unit = {},
    onNothingSelected: () -> Unit = {}
    ){

    val themeStore by preferencesViewModel.themeFlow.collectAsState()
    val unitStore by preferencesViewModel.unitFlow.collectAsState()
    val theme by remember { mutableStateOf(themeStore) }
    val unit by remember { mutableStateOf(unitStore) }

    val darkTheme = when (theme) {
        "Dark" -> true
        "Light" -> false
        else -> isSystemInDarkTheme()
    }

    AndroidView(
        factory = { context ->
            PieChart(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )

                setUsePercentValues(true)
                description.isEnabled = false
                holeRadius = 40f
                setHoleColor(Color.Transparent.toArgb())
                transparentCircleRadius = 0f
                isRotationEnabled = false

                setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                    override fun onValueSelected(e: Entry?, h: Highlight?) {
                        onValueSelected(e as PieEntry)
                    }

                    override fun onNothingSelected() {
                        onNothingSelected()
                    }
                })

                legend.isEnabled = false
                legend.orientation = Legend.LegendOrientation.HORIZONTAL
                legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                legend.textColor = if(darkTheme) Color.White.toArgb() else Color.Black.toArgb()
                legend.setDrawInside(false)

                setEntryLabelColor(if(darkTheme) Color.Black.toArgb() else Color.White.toArgb())
            }

        },
        update = { pieChart ->

            val filteredEntries = entries.filter { it.value > 0 }

            val colorsMap = mapOf(
                "Legs" to if(darkTheme) Color.White.toArgb() else Color.Black.toArgb(),
                "Arms" to RedIndicator.toArgb(),
                "Back" to BlueIndicator.toArgb(),
                "Chest" to YellowIndicator.toArgb(),
                "Shoulders" to GreenIndicator.toArgb(),
                "Core" to OrangeIndicator.toArgb(),
                "Forearms" to PurpleIndicator.toArgb(),
                "Whole Body" to PinkIndicator.toArgb(),
                "Barbell" to BlueIndicator.toArgb(),
                "Dumbbell" to RedIndicator.toArgb(),
                "Bodyweight" to GreenIndicator.toArgb(),
                "Machine" to PurpleIndicator.toArgb(),
                "Cable" to YellowIndicator.toArgb()
            )
            val dataSet = PieDataSet(filteredEntries, "")
            dataSet.colors = filteredEntries.map { entry ->
                colorsMap[entry.label] ?: android.graphics.Color.GRAY
            }
            dataSet.selectionShift = 5f

            val data = PieData(dataSet)
            data.setValueFormatter(object : ValueFormatter() {
                override fun getFormattedValue(value: Float, ): String {
                    val entry = filteredEntries.find {
                        String.format("%.2f", it.value / filteredEntries.sumOf { it.value.toDouble() } * 100) ==
                                String.format("%.2f", value)
                    }
                    val originalValue = entry?.value?.let { Math.round(it) } ?: 0f
                    return if(isWeight){
                        "$originalValue $unit (${String.format("%.1f", value)}%)"
                    }else{
                        "$originalValue sets (${String.format("%.1f", value)}%)"
                    }

                }
            })
            data.setValueTextSize(12f)
            data.setValueTextColor(if(darkTheme) Color.Black.toArgb() else Color.White.toArgb())

            pieChart.data = data
            pieChart.invalidate()
        }
    )

}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun PieChartViewPreview(){
    val pieEntries = listOf(
        PieEntry(30f, "Legs"),
        PieEntry(20f, "Arms"),
        PieEntry(15f, "Back"),
        PieEntry(35f, "Chest")
    )
    Workout_App_2Theme {
      //  PieChartView(entries = pieEntries, isWeight = true, preferencesViewModel = PreferencesViewModel(
      //      PreferencesDataStore(LocalContext.current)
     //   ))
    }
}


