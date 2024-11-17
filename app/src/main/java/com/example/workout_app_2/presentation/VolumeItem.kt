package com.example.workout_app_2.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.workout_app_2.data.ExerciseEntity
import com.example.workout_app_2.data.ExerciseSet
import com.example.workout_app_2.ui.theme.Workout_App_2Theme

@Composable
fun VolumeItem(
    volumeDataList: List<VolumeData>,
    bodyPart: String,
    isWeight: Boolean,
    unit: String
){
    Surface(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 6.dp)
        .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ){
                Text(
                    modifier = Modifier
                        .padding(vertical = 8.dp, horizontal = 16.dp),
                    text = if(bodyPart == "") "Choose a sector" else bodyPart,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            val sortedVolumeDataList: List<VolumeData> = if(isWeight){
                volumeDataList.sortedByDescending { it.totalVolume }
            }else{
                volumeDataList.sortedByDescending { it.totalSets }
            }
            sortedVolumeDataList.forEach{volumeData ->
                VolumeExerciseItem(
                    volumeData = volumeData,
                    isWeight = isWeight,
                    unit = unit
                )
            }
        }
    }
}

@Preview
@Composable
fun VolumeItemPreview(){
    Workout_App_2Theme {
        VolumeItem(bodyPart = "Chest", volumeDataList = listOf(), isWeight = true, unit = "KG")
    }
}