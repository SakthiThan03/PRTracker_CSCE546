package com.example.csce546_week1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.csce546_week1.data.local.WorkoutEntity
import com.example.csce546_week1.ui.theme.Csce546week1Theme
import com.example.csce546_week1.viewmodel.WorkoutViewModel
import java.text.SimpleDateFormat
import java.util.*



sealed class Screen(val route: String) {
    data object Home       : Screen("home")
    data object LogWorkout : Screen("log_workout")
    data object History    : Screen("history")
    data object Progress   : Screen("progress")
    data object EditWorkout : Screen("edit_workout/{workoutId}") {
        fun withId(id: Long) = "edit_workout/$id"
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { Csce546week1Theme { PRTrackerApp() } }
    }
}

@Composable
fun PRTrackerApp(vm: WorkoutViewModel = viewModel()) {
    val nav = rememberNavController()

    NavHost(nav, startDestination = Screen.Home.route) {

        composable(Screen.Home.route) {
            HomeScreen(
                onGoToLog      = { nav.navigate(Screen.LogWorkout.route) },
                onGoToHistory  = { nav.navigate(Screen.History.route) },
                onGoToProgress = { nav.navigate(Screen.Progress.route) }
            )
        }

        composable(Screen.LogWorkout.route) {
            LogWorkoutScreen(vm)
        }

        composable(Screen.History.route) {
            HistoryScreen(
                vm       = vm,
                onBack   = { nav.popBackStack() },
                onEdit   = { id -> nav.navigate(Screen.EditWorkout.withId(id)) }
            )
        }

        composable(
            route     = Screen.EditWorkout.route,
            arguments = listOf(navArgument("workoutId") { type = NavType.LongType })
        ) { back ->
            val id = back.arguments?.getLong("workoutId") ?: 0L
            EditWorkoutScreen(vm = vm, workoutId = id, onDone = { nav.popBackStack() })
        }

        composable(Screen.Progress.route) {
            ProgressScreen(vm = vm, onBack = { nav.popBackStack() })
        }
    }
}
fun Long.toDateString(): String =
    SimpleDateFormat("MMM dd, yyyy  HH:mm", Locale.getDefault()).format(Date(this))

fun Long.toDayString(): String =
    SimpleDateFormat("EEEE, MMM dd yyyy", Locale.getDefault()).format(Date(this))



@Composable
fun HomeScreen(
    onGoToLog: () -> Unit,
    onGoToHistory: () -> Unit,
    onGoToProgress: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("🏋️ PRTracker", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(32.dp))

        Button(onClick = onGoToLog,      Modifier.fillMaxWidth()) { Text("Log Workout") }
        Spacer(Modifier.height(12.dp))
        Button(onClick = onGoToHistory,  Modifier.fillMaxWidth()) { Text("Workout History") }
        Spacer(Modifier.height(12.dp))
        Button(onClick = onGoToProgress, Modifier.fillMaxWidth()) { Text("Progress / PRs") }
    }
}



@Composable
fun LogWorkoutScreen(vm: WorkoutViewModel) {
    var name   by rememberSaveable { mutableStateOf("") }
    var sets   by rememberSaveable { mutableStateOf("") }
    var reps   by rememberSaveable { mutableStateOf("") }
    var weight by rememberSaveable { mutableStateOf("") }
    var saved  by rememberSaveable { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Log Workout", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = name, onValueChange = { name = it; saved = false },
            label = { Text("Exercise Name") }, modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = sets, onValueChange = { sets = it; saved = false },
            label = { Text("Sets") }, modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = reps, onValueChange = { reps = it; saved = false },
            label = { Text("Reps") }, modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = weight, onValueChange = { weight = it; saved = false },
            label = { Text("Weight (lbs/kg)") }, modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                if (name.isNotBlank()) {
                    vm.addWorkout(
                        WorkoutEntity(
                            name   = name.trim(),
                            sets   = sets.toIntOrNull()   ?: 0,
                            reps   = reps.toIntOrNull()   ?: 0,
                            weight = weight.toFloatOrNull() ?: 0f
                            // createdAt defaults to System.currentTimeMillis()
                        )
                    )
                    name = ""; sets = ""; reps = ""; weight = ""
                    saved = true
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Save Workout") }

        if (saved) {
            Spacer(Modifier.height(8.dp))
            Text("✅ Workout saved!", color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
fun HistoryScreen(
    vm: WorkoutViewModel,
    onBack: () -> Unit,
    onEdit: (Long) -> Unit
) {
    val workouts by vm.workouts.collectAsState()

    // Group workouts by calendar day
    val grouped: Map<String, List<WorkoutEntity>> = remember(workouts) {
        workouts.groupBy { it.createdAt.toDayString() }
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Workout History", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))

        if (workouts.isEmpty()) {
            Text("No workouts logged yet.")
        } else {
            LazyColumn(Modifier.weight(1f)) {
                grouped.forEach { (day, entries) ->
                    item {
                        Text(
                            text = day,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    items(entries, key = { it.id }) { workout ->
                        WorkoutRow(
                            workout  = workout,
                            onEdit   = { onEdit(workout.id) },
                            onDelete = { vm.deleteWorkout(workout) }
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))
        Button(onClick = onBack, Modifier.fillMaxWidth()) { Text("Back") }
    }
}

@Composable
fun WorkoutRow(workout: WorkoutEntity, onEdit: () -> Unit, onDelete: () -> Unit) {
    var showConfirm by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(workout.name, fontWeight = FontWeight.SemiBold)
                Text(
                    "Sets: ${workout.sets}  ·  Reps: ${workout.reps}  ·  Weight: ${workout.weight}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    workout.createdAt.toDateString(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
            IconButton(onClick = onEdit)   { Icon(Icons.Default.Edit,   "Edit") }
            IconButton(onClick = { showConfirm = true }) { Icon(Icons.Default.Delete, "Delete") }
        }
    }

    if (showConfirm) {
        AlertDialog(
            onDismissRequest = { showConfirm = false },
            title   = { Text("Delete workout?") },
            text    = { Text("\"${workout.name}\" will be permanently removed.") },
            confirmButton = {
                TextButton(onClick = { onDelete(); showConfirm = false }) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { showConfirm = false }) { Text("Cancel") }
            }
        )
    }
}


@Composable
fun EditWorkoutScreen(vm: WorkoutViewModel, workoutId: Long, onDone: () -> Unit) {
    val workout by vm.workoutById(workoutId).collectAsState(initial = null)

    var name   by rememberSaveable { mutableStateOf("") }
    var sets   by rememberSaveable { mutableStateOf("") }
    var reps   by rememberSaveable { mutableStateOf("") }
    var weight by rememberSaveable { mutableStateOf("") }
    var loaded by rememberSaveable { mutableStateOf(false) }


    LaunchedEffect(workout) {
        if (workout != null && !loaded) {
            name   = workout!!.name
            sets   = workout!!.sets.toString()
            reps   = workout!!.reps.toString()
            weight = workout!!.weight.toString()
            loaded = true
        }
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Edit Workout", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(value = name,   onValueChange = { name = it },   label = { Text("Exercise Name") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = sets,   onValueChange = { sets = it },   label = { Text("Sets") },          modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = reps,   onValueChange = { reps = it },   label = { Text("Reps") },          modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = weight, onValueChange = { weight = it }, label = { Text("Weight") },        modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                workout?.let {
                    vm.updateWorkout(
                        it.copy(
                            name   = name.trim(),
                            sets   = sets.toIntOrNull()    ?: it.sets,
                            reps   = reps.toIntOrNull()    ?: it.reps,
                            weight = weight.toFloatOrNull() ?: it.weight
                        )
                    )
                    onDone()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled  = name.isNotBlank()
        ) { Text("Save Changes") }

        Spacer(Modifier.height(8.dp))
        OutlinedButton(onClick = onDone, Modifier.fillMaxWidth()) { Text("Cancel") }
    }
}

@Composable
fun ProgressScreen(vm: WorkoutViewModel, onBack: () -> Unit) {
    val names    by vm.exerciseNames.collectAsState()
    var selected by rememberSaveable { mutableStateOf<String?>(null) }
    val history  by remember(selected) {
        if (selected != null) vm.workoutsByName(selected!!)
        else kotlinx.coroutines.flow.flowOf(emptyList())
    }.collectAsState(initial = emptyList())

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Progress / PRs", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))

        if (names.isEmpty()) {
            Text("Log some workouts first to track progress.")
        } else {
            Text("Select an exercise:", style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(4.dp))

            LazyColumn(Modifier.weight(0.3f)) {
                items(names) { n ->
                    val isSelected = n == selected
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp)
                            .clickable { selected = n },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected)
                                MaterialTheme.colorScheme.primaryContainer
                            else
                                MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Text(n, Modifier.padding(12.dp))
                    }
                }
            }

            if (selected != null && history.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))
                val pr = history.maxByOrNull { it.weight }

                Text(
                    "📈 ${selected}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                pr?.let {
                    Text(
                        "🏆 PR: ${it.weight} lbs/kg  (${it.sets}×${it.reps})  on ${it.createdAt.toDateString()}",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(Modifier.height(8.dp))

                LazyColumn(Modifier.weight(0.7f)) {
                    items(history.reversed()) { w ->   // newest first
                        Row(
                            Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(w.createdAt.toDateString(), style = MaterialTheme.typography.bodySmall)
                            Text("${w.sets}×${w.reps} @ ${w.weight}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                        }
                        HorizontalDivider()
                    }
                }
            } else if (selected != null) {
                Spacer(Modifier.height(8.dp))
                Text("No history yet for \"$selected\".")
            }
        }

        Spacer(Modifier.height(8.dp))
        Button(onClick = onBack, Modifier.fillMaxWidth()) { Text("Back") }
    }
}
