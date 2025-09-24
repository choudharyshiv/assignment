package com.shadi.assignment.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profiles")
data class UserProfileEntity(
    @PrimaryKey val uuid: String,
    val firstName: String?,
    val lastName: String?,
    val imageUrl: String?,
    val gender: String?,
    val email: String?,
    val city: String?,
    val country: String?,
    val status: String? // "accepted", "declined", or null
)

