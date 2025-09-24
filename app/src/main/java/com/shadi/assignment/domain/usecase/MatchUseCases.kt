package com.shadi.assignment.domain.usecase

import com.shadi.assignment.data.repository.MatchRepository
import com.shadi.assignment.domain.model.Status

class GetMatchesUseCase(private val repository: MatchRepository) {
    fun invoke() = repository.getProfiles()
    suspend fun fetchRemote(page: Int, pageSize: Int) = repository.fetchAndCacheProfiles(page, pageSize)
}

class AcceptMatchUseCase(private val repository: MatchRepository) {
    suspend fun invoke(uuid: String) = repository.updateStatus(uuid, Status.ACCEPTED)
}

class DeclineMatchUseCase(private val repository: MatchRepository) {
    suspend fun invoke(uuid: String) = repository.updateStatus(uuid, Status.DECLINED)
}

