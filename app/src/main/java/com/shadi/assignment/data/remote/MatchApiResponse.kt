package com.shadi.assignment.data.remote

import com.google.gson.annotations.SerializedName

data class MatchApiResponse(
    @SerializedName("results") val results: List<UserProfileDto>
)

data class UserProfileDto(
    @SerializedName("login")
    val login: LoginDto,
    @SerializedName("name")
    val name: NameDto,
    @SerializedName("picture")
    val picture: PictureDto,
    @SerializedName("gender")
    val gender: String? = null,
    @SerializedName("email")
    val email: String? = null,
    @SerializedName("location")
    val location: LocationDto? = null
)

data class LoginDto(
    @SerializedName("uuid") val uuid: String
)

data class NameDto(
    @SerializedName("first") val first: String?,
    @SerializedName("last") val last: String?
)

data class PictureDto(
    @SerializedName("large") val large: String?
)

data class LocationDto(
    @SerializedName("city") val city: String?,
    @SerializedName("country") val country: String?
)