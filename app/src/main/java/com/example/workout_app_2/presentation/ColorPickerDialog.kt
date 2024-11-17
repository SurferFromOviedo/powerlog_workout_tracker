package com.example.workout_app_2.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.workout_app_2.ui.theme.Workout_App_2Theme
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.skydoves.colorpicker.compose.AlphaTile
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController


@Composable
fun ColorPickerDialog(
    initialColor: Color = Color.White,
    onDismiss: () -> Unit,
    onColorSelected: (Color) -> Unit
){
    val controller = rememberColorPickerController()

    Dialog(
        onDismissRequest = {onDismiss()},
        DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true, usePlatformDefaultWidth = false)
    ){
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.5f),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = CenterHorizontally
            ) {
                HsvColorPicker(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    controller = controller,
                    initialColor = initialColor,
                    onColorChanged = {
                    }
                )

                BrightnessSlider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .height(35.dp),
                    controller = controller,
                    initialColor = initialColor
                )

                AlphaTile(
                    modifier = Modifier
                        .size(80.dp)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    controller = controller
                )

                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    onClick = {
                        onColorSelected(controller.selectedColor.value)
                        onDismiss()
                    },
                    shape = RoundedCornerShape(40)
                ) {
                    Text(text = "Save")
                }
            }
        }
    }
}


@Preview
@Composable
fun ColorPickerDialogPreview(){
    Workout_App_2Theme {
        ColorPickerDialog(onDismiss = {}, onColorSelected = {})
    }

}
