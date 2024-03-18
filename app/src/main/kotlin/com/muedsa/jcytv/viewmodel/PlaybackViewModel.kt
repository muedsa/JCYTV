package com.muedsa.jcytv.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kuaishou.akdanmaku.data.DanmakuItemData
import com.muedsa.jcytv.KEY_DANMAKU_ALPHA
import com.muedsa.jcytv.KEY_DANMAKU_ENABLE
import com.muedsa.jcytv.KEY_DANMAKU_SCREEN_PART
import com.muedsa.jcytv.KEY_DANMAKU_SIZE_SCALE
import com.muedsa.jcytv.KEY_UPSCAYL_COVER_IMAGE_ENABLE
import com.muedsa.jcytv.model.AppSettingModel
import com.muedsa.jcytv.repository.DataStoreRepo
import com.muedsa.jcytv.room.dao.EpisodeProgressDao
import com.muedsa.jcytv.room.model.EpisodeProgressModel
import com.muedsa.jcytv.service.DanDanPlayApiService
import com.muedsa.model.LazyData
import com.muedsa.uitl.LogUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PlaybackViewModel @Inject constructor(
    private val danDanPlayApiService: DanDanPlayApiService,
    dateStoreRepo: DataStoreRepo,
    private val episodeProgressDao: EpisodeProgressDao
) : ViewModel() {

    val danmakuSettingLDSF: StateFlow<LazyData<AppSettingModel>> = dateStoreRepo.dataStore.data
        .map { prefs ->
            AppSettingModel(
                danmakuEnable = prefs[KEY_DANMAKU_ENABLE] ?: true,
                danmakuSizeScale = prefs[KEY_DANMAKU_SIZE_SCALE] ?: 140,
                danmakuAlpha = prefs[KEY_DANMAKU_ALPHA] ?: 100,
                danmakuScreenPart = prefs[KEY_DANMAKU_SCREEN_PART] ?: 100,
                upscaylCoverImageEnable = prefs[KEY_UPSCAYL_COVER_IMAGE_ENABLE] ?: false
            ).let { model ->
                LazyData.success(model)
            }
        }
        .catch {
            LogUtil.d(it)
            emit(LazyData.fail(it))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = LazyData.init()
        )

    private val _danmakuListLDSF = MutableStateFlow(LazyData.init<List<DanmakuItemData>>())
    val danmakuListLDSF: StateFlow<LazyData<List<DanmakuItemData>>> = _danmakuListLDSF

    private val _episodeProgressSF = MutableStateFlow(EpisodeProgressModel.Empty)
    val episodeProgressSF: StateFlow<EpisodeProgressModel> = _episodeProgressSF

    fun loadDanmakuList(episodeId: Long) {
        viewModelScope.launch {
            if (episodeId > 0) {
                _danmakuListLDSF.value = withContext(Dispatchers.IO) {
                    fetchDanmakuList(episodeId)
                }
            } else {
                _danmakuListLDSF.value = LazyData.success(emptyList())
            }
        }
    }

    private suspend fun fetchDanmakuList(episodeId: Long): LazyData<List<DanmakuItemData>> {
        return try {
            danDanPlayApiService.getComment(
                episodeId = episodeId,
                from = 0,
                withRelated = true,
                chConvert = 1
            ).comments
                .map {
                    val propArr = it.p.split(",")
                    val pos = (propArr[0].toFloat() * 1000).toLong()
                    val mode = if (propArr[1] == "1")
                        DanmakuItemData.DANMAKU_MODE_ROLLING
                    else if (propArr[1] == "4")
                        DanmakuItemData.DANMAKU_MODE_CENTER_BOTTOM
                    else if (propArr[1] == "5")
                        DanmakuItemData.DANMAKU_MODE_CENTER_TOP
                    else
                        DanmakuItemData.DANMAKU_MODE_ROLLING
                    val colorInt = propArr[2].toInt()
                    DanmakuItemData(
                        danmakuId = it.cid,
                        position = pos,
                        content = it.m,
                        mode = mode,
                        textSize = 25,
                        textColor = colorInt,
                        score = 9,
                        danmakuStyle = DanmakuItemData.DANMAKU_STYLE_NONE,
                        rank = 9
                    )
                }.let {
                    LazyData.success(it)
                }
        } catch (t: Throwable) {
            LogUtil.fb(t)
            LazyData.success(emptyList())
        }
    }

    fun loadEpisodeProgress(
        aid: Long,
        episodeTitle: String,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val titleHash = episodeTitle.hashCode()
            _episodeProgressSF.value = episodeProgressDao.getOneByAidAndTitleHash(aid, titleHash)
                ?: EpisodeProgressModel(aid, titleHash, episodeTitle, 0, 0, 0)
        }
    }

    fun saveEpisodeProgress(model: EpisodeProgressModel) {
        viewModelScope.launch(Dispatchers.IO) {
            LogUtil.d("save episode progress: ${model.progress}/${model.duration}")
            episodeProgressDao.upsert(model)
        }
    }
}