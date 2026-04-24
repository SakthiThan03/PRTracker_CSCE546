package com.example.csce546_week1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.csce546_week1.viewmodel.WorkoutViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberSaveable
import androidx.compose.runtime.setValue
import com.example.csce546_week1.data.local.WorkoutEntity
import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object LogWorkout : Screen("log_workout")
    data object History : Screen("history")
    data object Progress : Screen("progress")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PRTrackerApp()
        }
    }
}

@Composable
fun PRTrackerApp(
    workoutViewModel: WorkoutViewModel = viewModel()
) {
    val navController = rememberNavController()

    NavHost(
    navController = navController,
    startDestination = Screen.Home.route
) {
    composable(Screen.Home.route) {
        HomeScreen(
            onGoToLog = { navController.navigate(Screen.LogWorkout.route) },
            onGoToHistory = { navController.navigate(Screen.History.route) },
            onGoToProgress = { navController.navigate(Screen.Progress.route) }
        )
    }

    composable(Screen.LogWorkout.route) {
        LogWorkoutScreen(
            viewModel = workoutViewModel
        )
    }

    composable(Screen.History.route) {
        HistoryScreen(
            viewModel = workoutViewModel,
            onBack = { navController.popBackStack() },
            onWorkoutClick = { workoutId ->
                navController.navigate("detail/$workoutId")
            }
        )
    }

    composable(
        route = "detail/{workoutId}",
        arguments = listOf(navArgument("workoutId") { type = NavType.LongType })
    ) { backStackEntry ->
        val workoutId = backStackEntry.arguments?.getLong("workoutId") ?: 0L
        WorkoutDetailScreen(
            viewModel = workoutViewModel,
            workoutId = workoutId,
            onBack = { navController.popBackStack() }
        )
    }

    composable(Screen.Progress.route) {
        ProgressScreen(
            onBack = { navController.popBackStack() }
        )
    }
}

@Composable
fun HomeScreen(
    onGoToLog: () -> Unit,
    onGoToHistory: () -> Unit,
    onGoToProgress: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "PRTracker",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onGoToLog,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Log Workout")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onGoToHistory,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Workout History")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onGoToProgress,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Progress")
        }
    }
}

@Composable
fun LogWorkoutScreen(viewModel: WorkoutViewModel) {
    var name by rememberSaveable { mutableStateOf("") }
    var sets by rememberSaveable { mutableStateOf("") }
    var reps by rememberSaveable { mutableStateOf("") }
    var weight by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Log Workout",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Exercise Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = sets,
            onValueChange = { sets = it },
            label = { Text("Sets") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = reps,
            onValueChange = { reps = it },
            label = { Text("Reps") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = weight,
            onValueChange = { weight = it },
            label = { Text("Weight") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (name.isNotBlank()) {
                    val workout = WorkoutEntity(
                        name = name.trim(),
                        sets = sets.toIntOrNull() ?: 0,
                        reps = reps.toIntOrNull() ?: 0,
                        weight = weight.toFloatOrNull() ?: 0f
                    )

                    viewModel.addWorkout(workout)

                    name = ""
                    sets = ""
                    reps = ""
                    weight = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Workout")
        }
    }
}
@Composable
fun HistoryScreen(
    viewModel: WorkoutViewModel,
    onBack: () -> Unit,
    onWorkoutClick: (Long) -> Unit
) {
    val workouts by viewModel.workouts.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Workout History",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (workouts.isEmpty()) {
            Text("No workouts logged yet.")
        } else {
            LazyColumn {
                items(workouts) { workout ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Button(
                            onClick = { onWorkoutClick(workout.id) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(workout.name)
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text("Sets: ${workout.sets}  Reps: ${workout.reps}  Weight: ${workout.weight}")
                        Divider()
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back")
        }
    }
}

@Composable
fun WorkoutDetailScreen(
    viewModel: WorkoutViewModel,
    workoutId: Long,
    onBack: () -> Unit
) {
    val workout by viewModel.workoutById(workoutId).collectAsState(initial = null)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Workout Details",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (workout == null) {
            Text("Workout not found.")
        } else {
            Text("Exercise: ${workout!!.name}")
            Text("Sets: ${workout!!.sets}")
            Text("Reps: ${workout!!.reps}")
            Text("Weight: ${workout!!.weight}")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back")
        }
    }
}

@Composable
fun ProgressScreen(
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Progress",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("This screen can later show PR trends, charts, and weekly progress.")

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onBack) {
            Text("Back")
        }
    }
}