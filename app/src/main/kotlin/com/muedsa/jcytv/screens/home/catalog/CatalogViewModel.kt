package com.muedsa.jcytv.screens.home.catalog

import android.icu.util.Calendar
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.util.fastFirstOrNull
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muedsa.jcytv.model.JcySimpleVideoInfo
import com.muedsa.jcytv.repository.JcyRepo
import com.muedsa.model.LazyPagedList
import com.muedsa.uitl.LogUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.max

@HiltViewModel
class CatalogViewModel @Inject constructor(
    private val jcyRepo: JcyRepo
) : ViewModel() {

    val optionIdState = mutableStateOf(ID_OPTIONS.keys.first())
    val optionYearState = mutableStateOf<String?>(null)
    val optionLetterState = mutableStateOf<String?>(null)
    val optionByState = mutableStateOf<String?>(null)

    private val _animeLPSF = MutableStateFlow(
        LazyPagedList.new<Map<String, String>, JcySimpleVideoInfo>(
            buildQueryParams()
        )
    )
    val animeLPSF: StateFlow<LazyPagedList<Map<String, String>, JcySimpleVideoInfo>> = _animeLPSF

    fun catalog(lp: LazyPagedList<Map<String, String>, JcySimpleVideoInfo>) {
        viewModelScope.launch(Dispatchers.IO) {
            val loadingLP = lp.loadingNext()
            _animeLPSF.value = loadingLP
            _animeLPSF.value = fetchCatalog(loadingLP)
        }
    }

    fun catalogNew() {
        catalog(LazyPagedList.new(buildQueryParams()))
    }

    private fun fetchCatalog(
        lp: LazyPagedList<Map<String, String>, JcySimpleVideoInfo>
    ): LazyPagedList<Map<String, String>, JcySimpleVideoInfo> {
        return try {
            val pageNum = lp.nextPage
            jcyRepo.catalog(lp.query.toMutableMap().apply {
                this["page"] = pageNum.toString()
            }).let { videoList ->
                if (lp.list.isNotEmpty()) {
                    val mList = videoList.toMutableList()
                    val iterator = mList.iterator()
                    while (iterator.hasNext()) {
                        val videoInfo = iterator.next()
                        val fastFirst = lp.list.fastFirstOrNull { it.detailPagePath == videoInfo.detailPagePath }
                        if (fastFirst != null) {
                            LogUtil.d("fetchCatalog error, Duplicate video. \n$fastFirst \n$videoInfo")
                            iterator.remove()
                        }
                    }
                    lp.successNext(mList, if (mList.isEmpty()) pageNum else pageNum + 1)
                } else {
                    lp.successNext(videoList, if (videoList.isEmpty()) pageNum else pageNum + 1)
                }

            }
        } catch (t: Throwable) {
            LogUtil.fb(t)
            lp.failNext(t)
        }
    }

    fun resetCatalogOptions() {
        optionIdState.value = ID_OPTIONS.keys.first()
        optionYearState.value = null
        optionLetterState.value = null
        optionByState.value = null
        catalogNew()
    }

    private fun buildQueryParams(): Map<String, String> {
        return buildMap {
            put("id", optionIdState.value)
            optionYearState.value?.let { put("year", it) }
            optionLetterState.value?.let { put("letter", it) }
            optionByState.value?.let { put("by", it) }
        }
    }

    init {
        catalog(_animeLPSF.value)
    }

    companion object {
        val ID_OPTIONS: Map<String, String> = linkedMapOf(
            "20" to "新番放送",
            "4" to "追番计划",
            "21" to "欧美动漫",
            "3" to "动漫剧场",
            "22" to "国产动漫",
        )

        val YEAR_OPTIONS: Map<String, String> =
            (2000..max(Calendar.getInstance().get(Calendar.YEAR), 2024))
                .map { it.toString() }
                .associateBy { it }

        val LETTER_OPTIONS: Map<String, String> = listOf(
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
            "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"
        ).associateBy { it }

        val ORDER_BY_OPTIONS: Map<String, String> = mapOf(
            "time" to "时间排序",
            "hits" to "人气排序",
            "score" to "评分排序"
        )
    }
}