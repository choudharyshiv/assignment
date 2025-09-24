package com.shadi.assignment.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.shadi.assignment.domain.usecase.GetMatchesUseCase
import com.shadi.assignment.domain.usecase.AcceptMatchUseCase
import com.shadi.assignment.domain.usecase.DeclineMatchUseCase

class MatchViewModelFactory(
    private val getMatchesUseCase: GetMatchesUseCase,
    private val acceptMatchUseCase: AcceptMatchUseCase,
    private val declineMatchUseCase: DeclineMatchUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MatchViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MatchViewModel(getMatchesUseCase, acceptMatchUseCase, declineMatchUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
