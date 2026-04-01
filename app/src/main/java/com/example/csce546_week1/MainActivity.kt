package com.example.csce546_week1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.csce546_week1.model.ExerciseEntry
import com.example.csce546_week1.viewmodel.WorkoutViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val workoutViewModel: WorkoutViewModel = viewModel()
            PRTrackerApp(workoutViewModel)
        }
    }
}

@Composable
fun PRTrackerApp(viewModel: WorkoutViewModel) {
    var currentScreen by remember { mutableStateOf("home") }

    when (currentScreen) {
        "home" -> HomeScreen(
            onNavigateToLog = { currentScreen = "log" }
        )
        "log" -> LogWorkoutScreen(viewModel = viewModel)
    }
}

@Composable
fun HomeScreen(onNavigateToLog: () -> Unit) {
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

        Button(onClick = onNavigateToLog) {
            Text("Go to Log Workout")
        }
    }
}

@Composable
fun LogWorkoutScreen(viewModel: WorkoutViewModel) {
    var name by remember { mutableStateOf("") }
    var sets by remember { mutableStateOf("") }
    var reps by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }

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
            label = { Text("Exercise Name") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = sets,
            onValueChange = { sets = it },
            label = { Text("Sets") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = reps,
            onValueChange = { reps = it },
            label = { Text("Reps") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = weight,
            onValueChange = { weight = it },
            label = { Text("Weight") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (name.isNotBlank()) {
                    val entry = ExerciseEntry(
                        name = name,
                        sets = sets.toIntOrNull() ?: 0,
                        reps = reps.toIntOrNull() ?: 0,
                        weight = weight.toFloatOrNull() ?: 0f
                    )
                    viewModel.addExercise(entry)

                    name = ""
                    sets = ""
                    reps = ""
                    weight = ""
                }
            }
        ) {
            Text("Save Exercise")
        }
    }
}