package com.example.csce546_week1.data.repository

import com.example.csce546_week1.data.local.WorkoutDao
import com.example.csce546_week1.data.local.WorkoutEntity
import kotlinx.coroutines.flow.Flow

class WorkoutRepository(private val dao: WorkoutDao) {

    val allWorkouts: Flow<List<WorkoutEntity>> = dao.getAllWorkouts()

    val distinctExerciseNames: Flow<List<String>> = dao.getDistinctExerciseNames()

    suspend fun insert(workout: WorkoutEntity) = dao.insertWorkout(workout)

    suspend fun update(workout: WorkoutEntity) = dao.updateWorkout(workout)

    suspend fun delete(workout: WorkoutEntity) = dao.deleteWorkout(workout)

    suspend fun clear() = dao.clearAll()

    fun getWorkoutById(id: Long): Flow<WorkoutEntity?> = dao.getWorkoutById(id)

    fun getWorkoutsByName(name: String): Flow<List<WorkoutEntity>> =
        dao.getWorkoutsByName(name)
}
