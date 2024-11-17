package com.example.workout_app_2.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.workout_app_2.ui.theme.Workout_App_2Theme

@Composable
fun StatsItem(
    modifier: Modifier = Modifier,
    statsItemDataList: List<StatsItemData>
){
    val index  = rememberSaveable { mutableIntStateOf(0)}
    Surface(
        modifier = modifier
            .width(120.dp)
            .height(160.dp),
        color = MaterialTheme.colorScheme.secondaryContainer,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    onClick = {
                        index.intValue = (index.intValue + 1) % statsItemDataList.size
                    }
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ){
            Icon(
                modifier = Modifier
                    .size(30.dp),
                imageVector = statsItemDataList[index.intValue].icon,
                contentDescription = "Icon"
            )
            Text(
                text = statsItemDataList[index.intValue].value,
                textAlign = TextAlign.Center
            )

            Text(
                modifier = Modifier,
                text = statsItemDataList[index.intValue].label,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelLarge,
                fontSize = 16.sp
            )
            if(statsItemDataList[index.intValue].date != "" && statsItemDataList[index.intValue].date != null){
                Text(
                    text = "(${statsItemDataList[index.intValue].date})",
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun StatsItemPreview(){
    Workout_App_2Theme {
        val statsItemDataList = listOf(
            StatsItemData(
                icon = Icons.Outlined.AccessTime,
                value = "07H 30M",
                label = "Total time"
            ),
        )
        StatsItem(
            modifier = Modifier.padding(4.dp),
            statsItemDataList = statsItemDataList
        )
    }

}