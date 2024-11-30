package com.example.workout_app_2.presentation


import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Timeline
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.workout_app_2.data.DatabaseProvider
import com.example.workout_app_2.data.ExerciseRepository
import com.example.workout_app_2.data.FirebaseRepository
import com.example.workout_app_2.data.PreferencesDataStore
import com.example.workout_app_2.data.PreferencesDataStoreManager
import com.example.workout_app_2.data.PreferencesViewModel
import com.example.workout_app_2.data.PreferencesViewModelFactory
import com.example.workout_app_2.ui.theme.Workout_App_2Theme
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    private lateinit var preferencesDataStore: PreferencesDataStore

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {

        //Initialize datastore
        preferencesDataStore = PreferencesDataStoreManager.getInstance(this)
        super.onCreate(savedInstanceState)

        val firebaseRepository = FirebaseRepository()
        val database = DatabaseProvider.getDatabase(this)

        val preferencesViewModel = ViewModelProvider(
            this,
            PreferencesViewModelFactory(
                preferencesDataStore,
                firebaseRepository,
                database.exerciseDao(),
                database.workoutDao(),
                database.templateDao(),
                database.templateSetDao(),
                database.setDao()
            )
        )[PreferencesViewModel::class.java]

        enableEdgeToEdge()
        setContent {
            MainActivityContent(preferencesViewModel)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun MainActivityContent(
        preferencesViewModel: PreferencesViewModel

    ){
        val context = LocalContext.current
        var isInitialized by remember { mutableStateOf(false) }

        val unit by preferencesViewModel.unitFlow.collectAsState(initial = null)
        val theme by preferencesViewModel.themeFlow.collectAsState(initial = null)
        val dynamicColorPreferences by preferencesViewModel.dynamicColorFlow.collectAsState(initial = null)
        val primaryColorPreferences by preferencesViewModel.primaryColorFlow.collectAsState(initial = null)
        val screenOn by preferencesViewModel.screenOnFlow.collectAsState(initial = null)

        val dynamicColor = dynamicColorPreferences ?: false

        val darkTheme = when (theme) {
            "Dark" -> true
            "Light" -> false
            else -> isSystemInDarkTheme()
        }

        val primaryColor = primaryColorPreferences?.let { Color(it) }


        //Initialize exercises in database
        if(dynamicColorPreferences != null && primaryColorPreferences != null){
            LaunchedEffect(Unit) {
                if (unit != null && theme != null && screenOn != null) {
                    ExerciseRepository.initializeExercises(context)
                    delay(250)
                    isInitialized = true
                }
            }
        }


        //Until database is initialized, show loading screen
        Workout_App_2Theme(darkTheme = darkTheme, dynamicColor = dynamicColor, customPrimaryColor = primaryColor) {
            if(isInitialized){

                //Navigation
                val navController = rememberNavController()

                //Flag for ExerciseTemplateSelector
                //Will be given to WorkoutScreen
                var showDialog by rememberSaveable { mutableStateOf(false) }

                //Flag for WorkoutScreen, will be used to block buttons in settings screen
                var workoutInProgress by remember { mutableStateOf(false) }

                val activity = LocalContext.current as Activity
                LaunchedEffect(screenOn) {
                    if (screenOn == true) {
                        activity.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                    } else {
                        activity.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                    }
                }

                //Items for bottom navigation bar
                val items = listOf(
                    BottomNavigationItem(
                        title = "Workout",
                        icon = Icons.Filled.FitnessCenter,
                        unselectedIcon = Icons.Outlined.FitnessCenter,
                        route = "workout",
                        hasNews = false
                    ),
                    BottomNavigationItem(
                        title = "Calendar",
                        icon = Icons.Filled.CalendarMonth,
                        unselectedIcon = Icons.Outlined.CalendarMonth,
                        route = "calendar",
                        hasNews = false
                    ),
                    BottomNavigationItem(
                        title = "Stats",
                        icon = Icons.Filled.Timeline,
                        unselectedIcon = Icons.Outlined.Timeline,
                        route = "stats",
                        hasNews = false
                    ),
                    BottomNavigationItem(
                        title = "Settings",
                        icon = Icons.Filled.Settings,
                        unselectedIcon = Icons.Outlined.Settings,
                        route = "settings",
                        hasNews = false
                    ),

                    )

                //Get current route
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                //Find selected item index, comparing item route to current route
                val selectedItemIndex = items.indexOfFirst { it.route == currentRoute }.let {
                    if (it == -1) 0 else it
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        bottomBar = {
                            NavigationBar(
                                containerColor = MaterialTheme.colorScheme.surface,
                                contentColor = MaterialTheme.colorScheme.onSurface
                            ){
                                items.forEachIndexed { index, item ->
                                    NavigationBarItem(
                                        selected = selectedItemIndex == index,
                                        onClick = {
                                            if(currentRoute != item.route){
                                                navController.navigate(item.route){
                                                    popUpTo("workout"){
                                                        saveState = true
                                                    }
                                                    launchSingleTop = true
                                                    restoreState = true
                                                }
                                            }
                                        },
                                        label = { Text(text = item.title) },
                                        icon = {
                                            BadgedBox(
                                                badge ={
                                                    if (item.badgeCount != null) {
                                                        Badge { Text(text = item.badgeCount.toString()) }
                                                    }else if(item.hasNews){
                                                        Badge()
                                                    }
                                                }
                                            ){
                                                Icon(
                                                    imageVector = if (selectedItemIndex == index) item.icon else item.unselectedIcon,
                                                    contentDescription = item.title
                                                )
                                            }
                                        }
                                    )
                                }
                            }
                        },
                        floatingActionButton = {
                            AnimatedVisibility(
                                visible = selectedItemIndex == 0,
                                enter = fadeIn(),
                                exit = fadeOut()
                            ){
                                FloatingActionButton(
                                    onClick = {
                                        showDialog = true
                                    },
                                    shape = RoundedCornerShape(20.dp),
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    elevation = FloatingActionButtonDefaults.elevation(0.dp)
                                ) {
                                    Icon(Icons.Filled.Add, contentDescription = "Add Exercise")
                                }
                            }
                        }
                    ){ paddingValues ->
                        NavHost(
                            navController = navController,
                            startDestination = "workout",
                            modifier = Modifier.padding(paddingValues)
                        ) {
                            composable("workout", enterTransition = { fadeIn()}, exitTransition = { fadeOut()}) { WorkoutScreen(preferencesViewModel, showDialog = showDialog, onShowDialogChange = { newValue -> showDialog = newValue }, onSelectedExerciseChange = {workoutInProgress = it}) }
                            composable("calendar", enterTransition = { fadeIn()}, exitTransition = { fadeOut()}) { CalendarScreen(preferencesViewModel) }
                            composable("stats", enterTransition = { fadeIn()}, exitTransition = { fadeOut()}) { StatsScreen(preferencesViewModel) }
                            composable("settings", enterTransition = { fadeIn()}, exitTransition = { fadeOut()}) { SettingsScreen(workoutInProgress, preferencesViewModel) }
                        }
                    }
                }
            }else{
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    @Preview(
        showBackground = true,
        showSystemUi = true)
    @Composable
    fun PreviewMainActivity() {
        Workout_App_2Theme {
            //MainActivityContent(preferencesViewModel = PreferencesViewModel(preferencesDataStore))
        }
    }
}



