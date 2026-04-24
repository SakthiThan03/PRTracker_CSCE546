package com.example.csce546_week1.data.repository

import com.example.csce546_week1.data.local.WorkoutDao
import com.example.csce546_week1.data.local.WorkoutEntity
import kotlinx.coroutines.flow.Flow

class WorkoutRepository(private val dao: WorkoutDao) {

    val allWorkouts: Flow<List<WorkoutEntity>> = dao.getAllWorkouts()

    suspend fun insert(workout: WorkoutEntity) {
        dao.insertWorkout(workout)
    }

    suspend fun clear() {
        dao.clearAll()
    }
    fun getWorkoutById(id: Long) = dao.getWorkoutById(id)
}