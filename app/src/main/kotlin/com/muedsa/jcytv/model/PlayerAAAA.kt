package com.muedsa.jcytv.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlayerAAAA(
    @SerialName("flag") val flag: String = "",
    @SerialName("encrypt") val encrypt: Int = 0,
    @SerialName("trysee") val trySee: Int = 0,
    @SerialName("points") val points: Int = 0,
    @SerialName("link") val link: String = "",
    @SerialName("link_next") val linkNext: String = "",
    @SerialName("vod_data") val vodData: VodData,
    @SerialName("url") val url: String = "",
    @SerialName("url_next") val urlNext: String = "",
    @SerialName("from") val from: String = "",
    @SerialName("server") val server: String = "",
    @SerialName("note") val note: String = "",
    @SerialName("id") val id: String = "",
    @SerialName("sid") val sid: Int = 0,
    @SerialName("nid") val nid: Int = 0
)