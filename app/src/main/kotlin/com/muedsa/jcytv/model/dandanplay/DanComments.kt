package com.muedsa.jcytv.model.dandanplay

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DanComments(
    @SerialName("count") val count: Int = 0,
    @SerialName("comments") val comments: List<DanComment> = emptyList()
)
