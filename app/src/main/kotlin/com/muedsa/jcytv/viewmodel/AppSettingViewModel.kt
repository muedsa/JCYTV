package com.muedsa.jcytv.viewmodel

import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muedsa.jcytv.KEY_DANMAKU_ALPHA
import com.muedsa.jcytv.KEY_DANMAKU_ENABLE
import com.muedsa.jcytv.KEY_DANMAKU_SCREEN_PART
import com.muedsa.jcytv.KEY_DANMAKU_SIZE_SCALE
import com.muedsa.jcytv.KEY_UPSCAYL_COVER_IMAGE_ENABLE
import com.muedsa.jcytv.model.AppSettingModel
import com.muedsa.jcytv.repository.DataStoreRepo
import com.muedsa.model.LazyData
import com.muedsa.uitl.LogUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppSettingViewModel @Inject constructor(
    private val repo: DataStoreRepo
) : ViewModel() {

    val settingLDSF: StateFlow<LazyData<AppSettingModel>> = repo.dataStore.data
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

    fun changeDanmakuEnable(enable: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.dataStore.edit {
                it[KEY_DANMAKU_ENABLE] = enable
            }
        }
    }

    fun changeDanmakuSizeScale(value: Int) {
        if (value in 10..300) {
            viewModelScope.launch(Dispatchers.IO) {
                repo.dataStore.edit {
                    it[KEY_DANMAKU_SIZE_SCALE] = value
                }
            }
        }
    }

    fun changeDanmakuAlpha(value: Int) {
        if (value in 0..100) {
            viewModelScope.launch(Dispatchers.IO) {
                repo.dataStore.edit {
                    it[KEY_DANMAKU_ALPHA] = value
                }
            }
        }
    }

    fun changeDanmakuScreenPart(value: Int) {
        if (value in 10..100) {
            viewModelScope.launch(Dispatchers.IO) {
                repo.dataStore.edit {
                    it[KEY_DANMAKU_SCREEN_PART] = value
                }
            }
        }
    }

    fun changeUpscaylCoverImageEnable(enable: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.dataStore.edit {
                it[KEY_UPSCAYL_COVER_IMAGE_ENABLE] = enable
            }
        }
    }

}