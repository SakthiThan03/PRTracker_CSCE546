package com.example.csce546_week1.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.csce546_week1.data.local.AppDatabase
import com.example.csce546_week1.data.local.WorkoutEntity
import com.example.csce546_week1.data.repository.WorkoutRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WorkoutViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: WorkoutRepository =
        WorkoutRepository(AppDatabase.getDatabase(application).workoutDao())

    val workouts: StateFlow<List<WorkoutEntity>> =
        repository.allWorkouts.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    fun addWorkout(workout: WorkoutEntity) {
        viewModelScope.launch {
            repository.insert(workout)
        }
    }

    fun clearWorkouts() {
        viewModelScope.launch {
            repository.clear()
        }
    }

    fun workoutById(id: Long) = repository.getWorkoutById(id)
}