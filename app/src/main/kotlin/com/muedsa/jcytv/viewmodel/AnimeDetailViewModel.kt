package com.muedsa.jcytv.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muedsa.jcytv.model.dandanplay.DanAnimeInfo
import com.muedsa.jcytv.model.dandanplay.DanSearchAnime
import com.muedsa.jcytv.exception.DataRequestException
import com.muedsa.jcytv.model.JcyVideoDetail
import com.muedsa.jcytv.room.dao.EpisodeProgressDao
import com.muedsa.jcytv.room.dao.FavoriteAnimeDao
import com.muedsa.jcytv.room.model.FavoriteAnimeModel
import com.muedsa.jcytv.service.DanDanPlayApiService
import com.muedsa.jcytv.ui.nav.NavigationItems
import com.muedsa.jcytv.util.JcyHtmlTool
import com.muedsa.model.LazyData
import com.muedsa.model.LazyType
import com.muedsa.uitl.LogUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AnimeDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val danDanPlayApiService: DanDanPlayApiService,
    private val favoriteAnimeDao: FavoriteAnimeDao,
    private val episodeProgressDao: EpisodeProgressDao
)  : ViewModel() {

    private val _navAnimeIdFlow = savedStateHandle.getStateFlow(ANIME_ID_SAVED_STATE_KEY, "0")
    val animeIdSF = MutableStateFlow(_navAnimeIdFlow.value)

    private val _animeDetailLDSF = MutableStateFlow(LazyData.init<JcyVideoDetail>())
    val animeDetailLDSF: StateFlow<LazyData<JcyVideoDetail>> = _animeDetailLDSF

    val danSearchTitleSF: MutableStateFlow<String?> = MutableStateFlow(null)

    private val _danSearchAnimeListLDSF = MutableStateFlow(LazyData.init<List<DanSearchAnime>>())
    val danSearchAnimeListLDSF: StateFlow<LazyData<List<DanSearchAnime>>> = _danSearchAnimeListLDSF

    private val _danAnimeInfoLDSF = MutableStateFlow(LazyData.init<DanAnimeInfo>())
    val danAnimeInfoLDSF: StateFlow<LazyData<DanAnimeInfo>> = _danAnimeInfoLDSF

    private val _favoriteRefreshSF = MutableStateFlow(0)
    val favoriteModelSF = animeDetailLDSF.combine(_favoriteRefreshSF) { animeDetailLD, _ ->
        if (animeDetailLD.type == LazyType.SUCCESS) {
            animeDetailLD.data?.id?.let {
                favoriteAnimeDao.getById(it)
            }
        } else null
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    private val _watchedEpisodeTitleSetRefreshSF = MutableStateFlow(0)
    val watchedEpisodeTitleMapSF =
        animeDetailLDSF.combine(_watchedEpisodeTitleSetRefreshSF) { animeDetailLD, _ ->
            (if (animeDetailLD.type == LazyType.SUCCESS) {
                animeDetailLD.data?.id?.let {
                    episodeProgressDao.getListByAid(it)
                }
            } else null)?.associateBy({ it.title }, { it }) ?: emptyMap()
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )

    private fun animeDetail(aid: Long) {
        viewModelScope.launch {
            _animeDetailLDSF.value = LazyData.init()
            _animeDetailLDSF.value = withContext(Dispatchers.IO) {
                fetchAnimeDetail(aid)
            }
        }
    }

    private suspend fun fetchAnimeDetail(aid: Long): LazyData<JcyVideoDetail> {
        return try {
            LazyData.success(JcyHtmlTool.getVideoDetailById(aid))
        } catch (t: Throwable) {
            LogUtil.fb(t)
            LazyData.fail(t)
        }
    }

    private suspend fun fetchSearchDanAnime(title: String): LazyData<List<DanSearchAnime>> {
        return try {
            val resp = danDanPlayApiService.searchAnime(title)
            if (resp.errorCode != SUCCESS_CODE) {
                throw DataRequestException(resp.errorMessage)
            }
            LazyData.success(resp.animes)
        } catch (t: Throwable) {
            LogUtil.fb(t)
            LazyData.fail(t)
        }
    }


    fun danBangumi(danAnimeId: Int) {
        viewModelScope.launch {
            _danAnimeInfoLDSF.value = LazyData.init()
            _danAnimeInfoLDSF.value = withContext(Dispatchers.IO) {
                fetchDanBangumi(danAnimeId)
            }
        }
    }

    private suspend fun fetchDanBangumi(danAnimeId: Int): LazyData<DanAnimeInfo> {
        return try {
            val resp = danDanPlayApiService.getAnime(danAnimeId)
            if (resp.errorCode != SUCCESS_CODE) {
                throw DataRequestException(resp.errorMessage)
            }
            LazyData.success(resp.bangumi!!)
        } catch (t: Throwable) {
            LogUtil.fb(t)
            LazyData.fail(t)
        }
    }

    fun favorite(model: FavoriteAnimeModel, favorite: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            if (favorite) {
                favoriteAnimeDao.insertAll(model)
            } else {
                favoriteAnimeDao.delete(model)
            }
            _favoriteRefreshSF.update {
                it + 1
            }
        }
    }

    fun refreshWatchedEpisodeTitleSet() {
        _watchedEpisodeTitleSetRefreshSF.update {
            it + 1
        }
    }

    fun parseRealVideoUrlForDetailPlaySourceUrl(
        url: String,
        onSuccess: (String) -> Unit = {},
        onError: (Throwable) -> Unit = {}
    ) {
        viewModelScope.launch(context = Dispatchers.IO) {
            try {
                val rawPlaySource = JcyHtmlTool.getRawPlaySource(
                    JcyHtmlTool.getAbsoluteUrl(url)
                )
                LogUtil.fb("play source: $rawPlaySource")
                val realPlayUrl = JcyHtmlTool.getRealPlayUrl(rawPlaySource)
                withContext(Dispatchers.Main) {
                    onSuccess(realPlayUrl)
                }
            } catch (t: Throwable) {
                withContext(Dispatchers.Main) {
                    onError(t)
                }
            }
        }
    }

    init {
        viewModelScope.launch {
            _navAnimeIdFlow.collectLatest { navAnimeId ->
                animeIdSF.value = navAnimeId
            }
        }

        viewModelScope.launch {
            animeIdSF.collectLatest {
                val aid = it.toLong()
                animeDetail(aid)
            }
        }

        viewModelScope.launch {
            _animeDetailLDSF.collectLatest {
                if (it.type == LazyType.SUCCESS) {
                    danSearchTitleSF.value = it.data?.title
                }
            }
        }

        viewModelScope.launch {
            danSearchTitleSF.collectLatest {
                if (!it.isNullOrBlank()) {
                    _danSearchAnimeListLDSF.value = LazyData.init()
                    _danSearchAnimeListLDSF.value = fetchSearchDanAnime(it)
                }
            }
        }

        viewModelScope.launch {
            _danSearchAnimeListLDSF.collectLatest {
                if (it.type == LazyType.SUCCESS && it.data != null) {
                    if (it.data.isNotEmpty()) {
                        danBangumi(it.data[0].animeId)
                    } else {
                        _danAnimeInfoLDSF.value = LazyData(type = LazyType.SUCCESS)
                    }
                }
            }
        }
    }

    companion object {
        val ANIME_ID_SAVED_STATE_KEY: String = NavigationItems.Detail.args[0].name

        const val SUCCESS_CODE = 0
    }
}