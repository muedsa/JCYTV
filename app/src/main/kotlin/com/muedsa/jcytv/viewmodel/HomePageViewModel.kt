package com.muedsa.jcytv.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muedsa.jcytv.model.JcySimpleVideoInfo
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
class HomePageViewModel @Inject constructor() : ViewModel() {

    private val _homeRowsSF = MutableStateFlow(LazyData.init<List<Pair<String, List<JcySimpleVideoInfo>>>>())
    val homeRowsSF: StateFlow<LazyData<List<Pair<String, List<JcySimpleVideoInfo>>>>> = _homeRowsSF

    fun refreshHomeData() {
        viewModelScope.launch {
            _homeRowsSF.value = withContext(Dispatchers.IO) {
                fetchHomeRows()
            }
        }
    }

    private suspend fun fetchHomeRows(): LazyData<List<Pair<String, List<JcySimpleVideoInfo>>>> {
        return try {
            LazyData.success(JcyHtmlTool.getHomeVideoRows())
        } catch (t: Throwable) {
            LogUtil.fb(t)
            LazyData.fail(t)
        }
    }

    init {
        refreshHomeData()
    }
}