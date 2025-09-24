package com.shadi.assignment.data.repository

import com.shadi.assignment.data.local.UserProfileEntity
import com.shadi.assignment.data.remote.UserProfileDto
import com.shadi.assignment.domain.model.Status
import com.shadi.assignment.domain.model.UserProfile

fun UserProfileDto.toEntity(): UserProfileEntity = UserProfileEntity(
    uuid = login.uuid,
    firstName = name.first,
    lastName = name.last,
    imageUrl = picture.large,
    gender = gender,
    email = email,
    city = location?.city,
    country = location?.country,
    status = null
)

fun UserProfileEntity.toDomain(): UserProfile = UserProfile(
    uuid = uuid,
    firstName = firstName,
    lastName = lastName,
    imageUrl = imageUrl,
    gender = gender,
    email = email,
    city = city,
    country = country,
    status = when (status) {
        "accepted" -> Status.ACCEPTED
        "declined" -> Status.DECLINED
        else -> Status.NONE
    }
)

