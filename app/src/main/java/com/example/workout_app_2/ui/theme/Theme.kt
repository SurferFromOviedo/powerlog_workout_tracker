package com.example.workout_app_2.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

fun Color.lighten(factor: Float): Color {
    return Color(
        red = (this.red + (1 - this.red) * factor).coerceIn(0f, 1f),
        green = (this.green + (1 - this.green) * factor).coerceIn(0f, 1f),
        blue = (this.blue + (1 - this.blue) * factor).coerceIn(0f, 1f),
        alpha = this.alpha
    )
}

fun Color.darken(factor: Float): Color {
    return Color(
        red = (this.red * (1 - factor)).coerceIn(0f, 1f),
        green = (this.green * (1 - factor)).coerceIn(0f, 1f),
        blue = (this.blue * (1 - factor)).coerceIn(0f, 1f),
        alpha = this.alpha
    )
}


@Composable
fun Workout_App_2Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    customPrimaryColor: Color? = null,
    content: @Composable () -> Unit
) {

    var customColor: Color? = customPrimaryColor
    if (customColor == null){
        customColor = Purple40
    }
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> darkColorScheme(
            primary = customColor.lighten(0.4f),
            secondaryContainer = customColor.darken(0.7f),
            surface = customColor.darken(0.95f),
            background = customColor.darken(0.95f),
            onBackground = customColor.lighten(0.9f),
            onPrimary = customColor.darken(0.9f),
            primaryContainer = customColor.lighten(0.9f),


        )
        else -> lightColorScheme(
            primary = customColor,
            secondaryContainer = customColor.lighten(0.8f),
            surface = customColor.lighten(0.95f),
            background = customColor.lighten(0.95f),
            onBackground = customColor.darken(0.9f),
            onPrimary = customColor.lighten(0.9f),
            primaryContainer = customColor.darken(0.9f)


        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}