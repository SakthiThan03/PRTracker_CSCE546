package com.example.csce546_week1.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workouts")
data class WorkoutEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val name: String,
    val sets: Int,
    val reps: Int,
    val weight: Float
      val createdAt: Long = System.currentTimeMillis()
)