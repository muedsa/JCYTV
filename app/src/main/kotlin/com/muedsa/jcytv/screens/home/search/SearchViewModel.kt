package com.muedsa.jcytv.screens.home.search

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muedsa.jcytv.model.JcySimpleVideoInfo
import com.muedsa.jcytv.repository.JcyRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val jcyRepo: JcyRepo
) : ViewModel() {

    private val internalUiState = MutableSharedFlow<SearchScreenUiState>()
    val uiState = internalUiState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SearchScreenUiState.Done(emptyList())
    )

    fun searchAnime(query: String) {
        if (query.isNotBlank()) {
            viewModelScope.launch(Dispatchers.IO) {
                internalUiState.emit(SearchScreenUiState.Searching)
                val state = try {
                    val list = jcyRepo.searchVideos(query)
                    SearchScreenUiState.Done(list)
                } catch (throwable: Throwable) {
                    SearchScreenUiState.Error(throwable.message ?: "error", throwable)
                }
                internalUiState.emit(state)
            }
        }
    }
}

@Immutable
sealed interface SearchScreenUiState {
    data object Searching : SearchScreenUiState
    data class Error(val error: String, val exception: Throwable? = null) : SearchScreenUiState
    data class Done(val animeList: List<JcySimpleVideoInfo>) : SearchScreenUiState
}