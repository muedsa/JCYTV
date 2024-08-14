package com.muedsa.jcytv.screens.home.rank

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muedsa.jcytv.model.JcyRankList
import com.muedsa.jcytv.repository.JcyRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RankViewModel @Inject constructor(
    private val jcyRepo: JcyRepo
) : ViewModel() {

    private val internalUiState = MutableSharedFlow<RankScreenUiState>()
    val uiState = internalUiState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = RankScreenUiState.Loading
    )

    fun fetchRankList() {
        viewModelScope.launch(Dispatchers.IO) {
            internalUiState.emit(RankScreenUiState.Loading)
            val state = try {
                val list = jcyRepo.fetchRankList()
                RankScreenUiState.Ready(list)
            } catch (throwable: Throwable) {
                RankScreenUiState.Error(throwable.message ?: "error", throwable)
            }
            internalUiState.emit(state)
        }
    }

    init {
        fetchRankList()
    }
}

@Immutable
sealed interface RankScreenUiState {
    data object Loading : RankScreenUiState
    data class Error(val error: String, val exception: Throwable? = null) : RankScreenUiState
    data class Ready(val rankList: List<JcyRankList>) : RankScreenUiState
}