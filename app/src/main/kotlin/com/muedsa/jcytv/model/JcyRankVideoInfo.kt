package com.muedsa.jcytv.model

data class JcyRankVideoInfo(
    val title: String,
    val subTitle: String = "",
    val detailPagePath: String = "",
    val imageUrl: String = "",
    val hotNum: Int = 0,
    val index: Int = 0
) {
    val id: Long by lazy {
        detailPagePath.substringAfter("/index.php/vod/detail/id/").removeSuffix(".html").toLong()
    }
}