package com.example.workout_app_2.presentation

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.workout_app_2.ui.theme.Workout_App_2Theme
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase



class AuthActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val auth = Firebase.auth

        val theme = intent.getStringExtra("theme")
        val dynamicColor = intent.getBooleanExtra("dynamicColor", true)
        val primaryColor = intent.getIntExtra("primaryColor", 0xFF6650a4.toInt())
        val screenOn = intent.getBooleanExtra("screenOn", false)

        enableEdgeToEdge()
        setContent {
            val darkTheme: Boolean = when (theme) {
                "Dark" -> {
                    true
                }
                "Light" -> {
                    false
                }
                else -> {
                    isSystemInDarkTheme()
                }
            }
            val activity = LocalContext.current as Activity
            LaunchedEffect(screenOn) {
                if (screenOn) {
                    activity.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                } else {
                    activity.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                }
            }
            Workout_App_2Theme(darkTheme = darkTheme, dynamicColor = dynamicColor, customPrimaryColor = Color(primaryColor)) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AuthScreen(
                        modifier = Modifier.padding(innerPadding),
                        onSignIn = { email, password ->
                            auth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener{
                                    if(it.isSuccessful){
                                        val resultIntent = Intent()
                                        resultIntent.putExtra("wasJustSignedIn", true)
                                        setResult(Activity.RESULT_OK, resultIntent)
                                        finish()
                                    }else{
                                        println(it.exception)
                                    }
                                }
                        },
                        onRegister = { email, password ->
                            auth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener{
                                    if(it.isSuccessful){
                                        val resultIntent = Intent()
                                        resultIntent.putExtra("wasJustSignedIn", true)
                                        setResult(Activity.RESULT_OK, resultIntent)
                                        finish()
                                    }else{
                                        println(it.exception)
                                    }
                                }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun AuthScreen(
    modifier: Modifier = Modifier,
    onSignIn: (String, String) -> Unit = {_, _ -> },
    onRegister: (String, String) -> Unit = {_, _ -> }
){
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val passwordRepeat = remember { mutableStateOf("") }
    val isSingingIn = remember { mutableStateOf(true) }
    val label = remember { mutableStateOf("Sign in") }
    val textButtonText = remember { mutableStateOf("Do not have an account?") }
    when (isSingingIn.value) {
        true -> {
            label.value = "Sign up"
            textButtonText.value = "Do not have an account?"
        }
        false -> {
            label.value = "Register"
            textButtonText.value = "Already have an account?"
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Text(
            modifier = Modifier.padding(16.dp),
            text = label.value
        )
        TextField(
            modifier = Modifier.padding(bottom = 8.dp),
            value = email.value,
            onValueChange = { newValue ->
                email.value = newValue

            },
            label = {
                Text(text = "Email")
            }
        )
        TextField(
            modifier = Modifier.padding(vertical = 8.dp),
            value = password.value,
            onValueChange = { newValue ->
                password.value = newValue
            },
            label = {
                Text(text = "Password")
            }
        )
        if(!isSingingIn.value){
            TextField(
                modifier = Modifier
                    .padding(vertical = 8.dp),
                value = passwordRepeat.value,
                onValueChange = { newValue ->
                    passwordRepeat.value = newValue
                },
                label = {
                    Text(text = "Confirm password")
                },
            )
        }

        TextButton(
            onClick = {
                isSingingIn.value = !isSingingIn.value
            },

            ) {
            Text(text = textButtonText.value)
        }
        Button(
            modifier = Modifier.padding(vertical = 4.dp),
            onClick = {
                if(isSingingIn.value){
                    onSignIn(email.value, password.value)
                }else{
                    if(password.value == passwordRepeat.value){
                        onRegister(email.value, password.value)
                    }else{
                        println("Passwords do not match")
                    }
                }
            },
            shape = RoundedCornerShape(40)

        ){
            Text(
                text = label.value
            )
        }

    }

}


@Preview(showBackground = true)
@Composable
fun AuthScreenPreview() {
    Workout_App_2Theme {
        AuthScreen()
    }
}