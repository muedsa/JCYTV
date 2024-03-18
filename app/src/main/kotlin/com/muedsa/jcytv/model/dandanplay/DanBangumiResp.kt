package com.muedsa.jcytv.model.dandanplay

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DanBangumiResp(
    @SerialName("success") val success: Boolean = false,
    @SerialName("errorCode") val errorCode: Int = -1,
    @SerialName("errorMessage") val errorMessage: String = "",
    @SerialName("bangumi") val bangumi: DanAnimeInfo? = null
)
