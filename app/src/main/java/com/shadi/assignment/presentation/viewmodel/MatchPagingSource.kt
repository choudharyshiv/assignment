package com.shadi.assignment.presentation.viewmodel

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.shadi.assignment.AssignmentApp
import com.shadi.assignment.NetworkUtils
import com.shadi.assignment.domain.model.UserProfile
import com.shadi.assignment.domain.usecase.GetMatchesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first

class MatchPagingSource(
    private val getMatchesUseCase: GetMatchesUseCase,
) : PagingSource<Int, UserProfile>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UserProfile> {
        val page = params.key ?: 1
        return try {
            // Use application context directly for network check
            val isNetworkAvailable = NetworkUtils.isNetworkAvailable(AssignmentApp.instance.applicationContext)
            if (isNetworkAvailable) {
                // Fetch remote and cache
                getMatchesUseCase.fetchRemote(page, params.loadSize)
            }
            // Always load from local DB
            val profiles = getMatchesUseCase.invoke().first()
            LoadResult.Page(
                data = profiles,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (profiles.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            // Try to load from local DB if error occurs
            val profiles = getMatchesUseCase.invoke().first()
            if (profiles.isNotEmpty()) {
                return LoadResult.Page(
                    data = profiles,
                    prevKey = if (page == 1) null else page - 1,
                    nextKey = if (profiles.isEmpty()) null else page + 1
                )
            }
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, UserProfile>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
