package com.shadi.assignment.data.repository

import com.shadi.assignment.data.local.UserProfileDao
import com.shadi.assignment.data.local.UserProfileEntity
import com.shadi.assignment.data.remote.MatchApiService
import com.shadi.assignment.domain.model.UserProfile
import com.shadi.assignment.domain.model.Status
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MatchRepository(
    private val apiService: MatchApiService,
    private val userProfileDao: UserProfileDao
) {
    fun getProfiles(): Flow<List<UserProfile>> =
        userProfileDao.getAllProfiles().map { entities ->
            entities.map { it.toDomain() }
        }

    suspend fun fetchAndCacheProfiles(page: Int, pageSize: Int) {
        val response = apiService.getMatches(pageSize, page)
        if (response.isSuccessful) {
            val profiles = response.body()?.results?.map { it.toEntity() } ?: emptyList()
            userProfileDao.insertProfiles(profiles)
        }
    }

    suspend fun updateStatus(uuid: String, status: Status) {
        userProfileDao.updateStatus(uuid, status.name.lowercase())
    }
}
