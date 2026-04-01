package com.example.csce546_week1.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.csce546_week1.model.ExerciseEntry

class WorkoutViewModel : ViewModel() {

    private val _exercises = MutableStateFlow<List<ExerciseEntry>>(emptyList())
    val exercises: StateFlow<List<ExerciseEntry>> = _exercises

    fun addExercise(entry: ExerciseEntry) {
        _exercises.value = _exercises.value + entry
    }
}