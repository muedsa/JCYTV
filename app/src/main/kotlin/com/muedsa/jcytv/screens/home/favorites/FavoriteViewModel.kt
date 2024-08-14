package com.muedsa.jcytv.screens.home.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muedsa.jcytv.room.dao.FavoriteAnimeDao
import com.muedsa.jcytv.room.model.FavoriteAnimeModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val dao: FavoriteAnimeDao
) : ViewModel() {

    val favoriteAnimeSF = dao.flowAll().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    fun remove(model: FavoriteAnimeModel) {
        viewModelScope.launch {
            dao.delete(model)
        }
    }

}