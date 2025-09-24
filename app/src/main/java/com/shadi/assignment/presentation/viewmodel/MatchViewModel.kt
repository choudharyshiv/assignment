package com.shadi.assignment.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.PagingConfig
import androidx.paging.Pager
import androidx.paging.cachedIn
import com.shadi.assignment.domain.model.UserProfile
import com.shadi.assignment.domain.usecase.AcceptMatchUseCase
import com.shadi.assignment.domain.usecase.DeclineMatchUseCase
import com.shadi.assignment.domain.usecase.GetMatchesUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class MatchViewModel(
    private val getMatchesUseCase: GetMatchesUseCase,
    private val acceptMatchUseCase: AcceptMatchUseCase,
    private val declineMatchUseCase: DeclineMatchUseCase
) : ViewModel() {
    private val refreshTrigger = MutableStateFlow(0)

    @OptIn(ExperimentalCoroutinesApi::class)
    val matches: Flow<PagingData<UserProfile>> = refreshTrigger.flatMapLatest {
        Pager(
            config = PagingConfig(pageSize = 10),
            pagingSourceFactory = { MatchPagingSource(getMatchesUseCase) }
        ).flow.cachedIn(viewModelScope)
    }

    fun acceptMatch(uuid: String) {
        viewModelScope.launch {
            acceptMatchUseCase.invoke(uuid)
            refreshTrigger.value++ // trigger refresh by incrementing
        }
    }

    fun declineMatch(uuid: String) {
        viewModelScope.launch {
            declineMatchUseCase.invoke(uuid)
            refreshTrigger.value++ // trigger refresh by incrementing
        }
    }
}
