package com.example.workout_app_2.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.workout_app_2.ui.theme.Workout_App_2Theme

@Composable
fun Spinner(
    items: List<String>,
    selectedItem: String,
    onItemSelected: (String) -> Unit,
    modifier: Modifier = Modifier
){
    var expanded by remember { mutableStateOf(false)}

    Box(modifier = modifier
        .height(35.dp)
        .fillMaxWidth(),
        ){
        Button(
            onClick = { expanded = true },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)),
            modifier = Modifier
                .fillMaxSize()
        ) {
            if (selectedItem.isEmpty()) {
                Text(
                    text = items[0],
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }else{
                Text(
                    text = selectedItem,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Box(
            modifier = Modifier
                .offset(y = 35.dp)
        ){
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
            ) {
                items.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(text = item) },
                        onClick = {
                            onItemSelected(item)
                            expanded = false
                        })
                }
            }

        }
    }
}


@Preview(
    showBackground = true)
@Composable
fun PreviewSpinner(){
    Workout_App_2Theme {
        Spinner(items = listOf("Item 1", "Item 2", "Item 3"), selectedItem = "", onItemSelected = {})
    }
}