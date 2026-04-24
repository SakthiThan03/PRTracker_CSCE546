package com.example.csce546_week1.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {

    @Insert
    suspend fun insertWorkout(workout: WorkoutEntity)

    @Query("SELECT * FROM workouts ORDER BY id DESC")
    fun getAllWorkouts(): Flow<List<WorkoutEntity>>

    @Query("DELETE FROM workouts")
    suspend fun clearAll()

    @Query("SELECT * FROM workouts WHERE id = :id LIMIT 1")
fun getWorkoutById(id: Long): Flow<WorkoutEntity?>

@Query("SELECT * FROM workouts ORDER BY createdAt DESC")
fun getAllWorkouts(): Flow<List<WorkoutEntity>>
}