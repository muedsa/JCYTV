package com.muedsa.jcytv.screens.setting

import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muedsa.jcytv.KEY_DANMAKU_ALPHA
import com.muedsa.jcytv.KEY_DANMAKU_ENABLE
import com.muedsa.jcytv.KEY_DANMAKU_MERGE_ENABLE
import com.muedsa.jcytv.KEY_DANMAKU_SCREEN_PART
import com.muedsa.jcytv.KEY_DANMAKU_SIZE_SCALE
import com.muedsa.jcytv.model.AppSettingModel
import com.muedsa.jcytv.repository.DataStoreRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppSettingViewModel @Inject constructor(
    private val repo: DataStoreRepo
) : ViewModel() {

    val settingStateFlow: StateFlow<AppSettingModel?> = repo.dataStore.data
        .map { prefs ->
            AppSettingModel.fromPreferences(prefs)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    fun changeDanmakuEnable(enable: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.dataStore.edit {
                it[KEY_DANMAKU_ENABLE] = enable
            }
        }
    }

    fun changeDanmakuMergeEnable(enable: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.dataStore.edit {
                it[KEY_DANMAKU_MERGE_ENABLE] = enable
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

}