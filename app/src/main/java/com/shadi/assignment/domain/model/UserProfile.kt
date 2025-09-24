package com.shadi.assignment.domain.model

data class UserProfile(
    val uuid: String,
    val firstName: String?,
    val lastName: String?,
    val imageUrl: String?,
    val gender: String?,
    val email: String?,
    val city: String?,
    val country: String?,
    val status: Status?
)

enum class Status {
    ACCEPTED, DECLINED, NONE
}

