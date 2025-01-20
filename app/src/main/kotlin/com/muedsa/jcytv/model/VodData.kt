package com.muedsa.jcytv.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VodData(
    @SerialName("vod_name") val vodName: String = "",
    @SerialName("vod_actor") val vodActor: String = "",
    @SerialName("vod_director") val vodDirector: String = "",
    @SerialName("vod_class") val vodClass: String = ""
)