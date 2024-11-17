package com.example.workout_app_2.presentation

import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavigationItem(
    val title: String,
    val icon: ImageVector,
    val unselectedIcon: ImageVector,
    val route: String,
    val hasNews: Boolean,
    val badgeCount: Int? = null
)