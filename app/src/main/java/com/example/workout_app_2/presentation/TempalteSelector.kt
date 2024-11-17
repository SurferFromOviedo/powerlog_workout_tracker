package com.example.workout_app_2.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.workout_app_2.data.WorkoutTemplate
import com.example.workout_app_2.ui.theme.Workout_App_2Theme

@Composable
fun TemplateSelector(
    templates: List<WorkoutTemplate>,
    onDismiss: () -> Unit,
    onTemplateSelected: (WorkoutTemplate) -> Unit
){
    var searchQuery by remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = {onDismiss()},
        DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true, usePlatformDefaultWidth = false)
    ){
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.8f),

            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Templates",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(16.dp))

                Box(modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(50.dp))
                ){
                    BasicTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(35.dp),
                        value = searchQuery,
                        onValueChange = {searchQuery = it},
                        decorationBox = { innerTextField ->
                            Box(
                                modifier = Modifier
                                    .background(
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 8.dp)
                            ) {
                                innerTextField()
                            }
                        }
                    )

                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp, horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween) {

                        if(searchQuery.isEmpty()){
                            Text(modifier = Modifier,
                                text = "Search Template",
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                                fontSize = 14.sp
                            )
                        }else{
                            Text(modifier = Modifier,
                                text = "               ",
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                                fontSize = 14.sp
                            )
                        }


                        Icon(modifier = Modifier
                            .padding(vertical = 1.dp),
                            imageVector = Icons.Outlined.Search,
                            contentDescription = "Icon Search")

                    }
                }

                Spacer(modifier = Modifier.height(5.dp))

                val filteredTemplates = templates.filter {template ->
                    (searchQuery.isEmpty() || template.name.contains(searchQuery, ignoreCase = true))
                }

                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(filteredTemplates){ template ->
                        TemplateSelectorItem(
                            template = template,
                            onClick = {
                                onTemplateSelected(template)
                            }
                        )
                    }
                }

            }
        }
    }
}

@Preview
@Composable
fun TemplateSelectorPreview() {
    Workout_App_2Theme {
        TemplateSelector(
            templates = listOf(),
            onDismiss = {},
            onTemplateSelected = {}
        )
    }
}
