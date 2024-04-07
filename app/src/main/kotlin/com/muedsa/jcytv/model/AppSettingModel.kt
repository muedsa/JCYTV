package com.muedsa.jcytv.model

import androidx.datastore.preferences.core.Preferences
import com.muedsa.jcytv.KEY_DANMAKU_ALPHA
import com.muedsa.jcytv.KEY_DANMAKU_ENABLE
import com.muedsa.jcytv.KEY_DANMAKU_MERGE_ENABLE
import com.muedsa.jcytv.KEY_DANMAKU_SCREEN_PART
import com.muedsa.jcytv.KEY_DANMAKU_SIZE_SCALE
import com.muedsa.jcytv.KEY_UPSCAYL_COVER_IMAGE_ENABLE

data class AppSettingModel(
    val danmakuEnable: Boolean,
    val danmakuMergeEnable: Boolean,
    val danmakuSizeScale: Int,
    val danmakuAlpha: Int,
    val danmakuScreenPart: Int,
    val upscaylCoverImageEnable: Boolean
) {

    companion object {

        fun fromPreferences(prefs: Preferences): AppSettingModel =
            AppSettingModel(
                danmakuEnable = prefs[KEY_DANMAKU_ENABLE] ?: true,
                danmakuMergeEnable = prefs[KEY_DANMAKU_MERGE_ENABLE] ?: false,
                danmakuSizeScale = prefs[KEY_DANMAKU_SIZE_SCALE] ?: 140,
                danmakuAlpha = prefs[KEY_DANMAKU_ALPHA] ?: 100,
                danmakuScreenPart = prefs[KEY_DANMAKU_SCREEN_PART] ?: 100,
                upscaylCoverImageEnable = prefs[KEY_UPSCAYL_COVER_IMAGE_ENABLE] ?: false
            )

    }

}