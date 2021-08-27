package com.udacity.asteroidradar.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "asteroids")
data class AsteroidDatabaseEntity constructor(
    @PrimaryKey(autoGenerate = true)
    val id: Long? ,
    val codename: String,
    val closeApproachDate: String,
    val absoluteMagnitude: Double,
    val estimatedDiameter: Double,
    val relativeVelocity: Double,
    val distanceFromEarth: Double,
    val isPotentiallyHazardous: Boolean
)

