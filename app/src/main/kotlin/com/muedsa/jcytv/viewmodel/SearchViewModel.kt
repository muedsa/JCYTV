package com.muedsa.jcytv.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muedsa.jcytv.model.JcySimpleVideoInfo
import com.muedsa.jcytv.util.JcyDocTool
import com.muedsa.model.LazyData
import com.muedsa.uitl.LogUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor() : ViewModel() {

    val searchTextSF = MutableStateFlow("")
    private val _searchAnimeLDSF = MutableStateFlow(LazyData.success<List<JcySimpleVideoInfo>>(emptyList()))
    val searchAnimeLDSF: StateFlow<LazyData<List<JcySimpleVideoInfo>>> = _searchAnimeLDSF

    fun searchAnime(query: String) {
        if (query.isNotBlank()) {
            viewModelScope.launch {
                _searchAnimeLDSF.value = LazyData.init()
                _searchAnimeLDSF.value = withContext(Dispatchers.IO) {
                    fetchSearch(query)
                }
            }
        }
    }

    private suspend fun fetchSearch(
        query: String
    ): LazyData<List<JcySimpleVideoInfo>> {
        return try {
            LazyData.success(JcyDocTool.searchVideo(query))
        } catch (t: Throwable) {
            LogUtil.fb(t)
            LazyData.fail(t)
        }
    }
}