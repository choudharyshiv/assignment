package com.shadi.assignment.presentation.viewmodel

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.shadi.assignment.domain.model.UserProfile
import com.shadi.assignment.domain.usecase.GetMatchesUseCase
import kotlinx.coroutines.flow.first

class MatchPagingSource(
    private val getMatchesUseCase: GetMatchesUseCase
) : PagingSource<Int, UserProfile>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UserProfile> {
        val page = params.key ?: 1
        return try {
            // Fetch remote and cache
            getMatchesUseCase.fetchRemote(page, params.loadSize)
            // Get from local DB
            val profiles = getMatchesUseCase.invoke().first()
            LoadResult.Page(
                data = profiles,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (profiles.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
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

