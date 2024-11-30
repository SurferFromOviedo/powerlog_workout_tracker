package com.example.workout_app_2.presentation

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun SyncDialog(
    onImport: () -> Unit,
    onOverwrite: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = "Data Synchronization") },
        text = { Text(text = "Data exists in Cloud. What would you like to do?") },
        confirmButton = {
            Button(onClick = onImport) {
                Text(text = "Import from Firestore")
            }
        },
        dismissButton = {
            Button(onClick = onOverwrite) {
                Text(text = "Overwrite Firestore")
            }
        }
    )
}