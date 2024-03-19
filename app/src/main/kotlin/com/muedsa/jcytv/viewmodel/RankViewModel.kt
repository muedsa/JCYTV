package com.muedsa.jcytv.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muedsa.jcytv.model.JcyRankVideoInfo
import com.muedsa.jcytv.util.JcyHtmlTool
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
class RankViewModel @Inject constructor() : ViewModel() {

    private val _rankListLDSF = MutableStateFlow(LazyData.init<List<Pair<String, List<JcyRankVideoInfo>>>>())
    val rankListLDSF: StateFlow<LazyData<List<Pair<String, List<JcyRankVideoInfo>>>>> = _rankListLDSF

    fun fetchRankList() {
        viewModelScope.launch {
            _rankListLDSF.value = LazyData.init()
            _rankListLDSF.value = withContext(Dispatchers.IO) {
                rankList()
            }
        }
    }

    private suspend fun rankList(): LazyData<List<Pair<String, List<JcyRankVideoInfo>>>> {
        return try {
            LazyData.success(JcyHtmlTool.rankList())
        } catch (t: Throwable) {
            LogUtil.fb(t)
            LazyData.fail(t)
        }
    }

    init {
        fetchRankList()
    }
}