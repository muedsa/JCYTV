package com.muedsa.jcytv.model.dandanplay

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DanAnimeTitle(
    @SerialName("language") val language: String,
    @SerialName("title") val title: String
)
