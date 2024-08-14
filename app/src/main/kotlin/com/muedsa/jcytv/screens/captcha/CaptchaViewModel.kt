package com.muedsa.jcytv.screens.captcha

import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muedsa.jcytv.KEY_CAPTCHA_GUARD_OK
import com.muedsa.jcytv.repository.DataStoreRepo
import com.muedsa.jcytv.screens.home.main.MainScreenUiState
import com.muedsa.jcytv.util.JcyRotateCaptchaTool
import com.muedsa.uitl.LogUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class CaptchaViewModel @Inject constructor(
    private val dataStoreRepo: DataStoreRepo
) : ViewModel() {

    private val internalUiState = MutableSharedFlow<CaptchaState>()
    val uiState = internalUiState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = MainScreenUiState.Loading
    )

    fun ready(guard: String?) {
        viewModelScope.launch {
            if (guard.isNullOrEmpty()) {
                internalUiState.emit(CaptchaState.Retry)
            } else {
                internalUiState.emit(CaptchaState.Ready(guard))
            }
        }
    }

    fun error() {
        viewModelScope.launch {
            internalUiState.emit(CaptchaState.Retry)
        }
    }

    fun validate(guard: String, degrees: Float) {
        viewModelScope.launch {
            internalUiState.emit(CaptchaState.Validating)
            withContext(Dispatchers.IO) {
                val state = try {
                    val guardOk = JcyRotateCaptchaTool.getGuardOk(guard, degrees)
                    if (!guardOk.isNullOrEmpty()) {
                        dataStoreRepo.dataStore.edit {
                            it[KEY_CAPTCHA_GUARD_OK] = guardOk
                        }
                        CaptchaState.Success
                    } else {
                        CaptchaState.Retry
                    }
                } catch (throwable: Throwable) {
                    LogUtil.d(throwable)
                    CaptchaState.Retry
                }
                internalUiState.emit(state)
            }
        }
    }
}

sealed interface CaptchaState {
    data object Validating: CaptchaState
    data class Ready(val guard: String): CaptchaState
    data object Retry: CaptchaState
    data object Success: CaptchaState
}