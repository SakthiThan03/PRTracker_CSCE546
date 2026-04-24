package com.example.csce546_week1.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {

    @Insert
    suspend fun insertWorkout(workout: WorkoutEntity)

    @Update
    suspend fun updateWorkout(workout: WorkoutEntity)

    @Delete
    suspend fun deleteWorkout(workout: WorkoutEntity)

   
    @Query("SELECT * FROM workouts ORDER BY createdAt DESC")
    fun getAllWorkouts(): Flow<List<WorkoutEntity>>

    @Query("SELECT * FROM workouts WHERE id = :id LIMIT 1")
    fun getWorkoutById(id: Long): Flow<WorkoutEntity?>

   
    @Query("SELECT * FROM workouts WHERE name = :exerciseName ORDER BY createdAt ASC")
    fun getWorkoutsByName(exerciseName: String): Flow<List<WorkoutEntity>>

    
    @Query("SELECT DISTINCT name FROM workouts ORDER BY name ASC")
    fun getDistinctExerciseNames(): Flow<List<String>>

    @Query("DELETE FROM workouts")
    suspend fun clearAll()
}
