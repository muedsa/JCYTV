package com.muedsa.jcytv.screens.home.main

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muedsa.jcytv.model.JcyVideoRow
import com.muedsa.jcytv.repository.JcyRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val jcyRepo: JcyRepo
) : ViewModel() {

    private val internalUiState = MutableSharedFlow<MainScreenUiState>()
    val uiState = internalUiState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = MainScreenUiState.Loading
    )

    fun refreshData() {
        viewModelScope.launch(Dispatchers.IO) {
            internalUiState.emit(MainScreenUiState.Loading)
            val state = try {
                val rows = jcyRepo.fetchHomeVideoRows()
                MainScreenUiState.Ready(rows)
            } catch (throwable: Throwable) {
                MainScreenUiState.Error(throwable.message ?: "error", throwable)
            }
            internalUiState.emit(state)
        }
    }

    init {
        refreshData()
    }
}

@Immutable
sealed interface MainScreenUiState {
    data object Loading: MainScreenUiState
    data class Error(val error: String, val exception: Throwable? = null): MainScreenUiState
    data class Ready(val rows: List<JcyVideoRow>): MainScreenUiState
}