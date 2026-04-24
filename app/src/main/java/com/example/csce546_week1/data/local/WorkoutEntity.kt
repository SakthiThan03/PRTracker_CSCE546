package com.example.csce546_week1.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workouts")
data class WorkoutEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val name: String,
    val sets: Int,
    val reps: Int,
    val weight: Float,
    // Timestamp in milliseconds — used for date grouping and ordering
    val createdAt: Long = System.currentTimeMillis()
)
