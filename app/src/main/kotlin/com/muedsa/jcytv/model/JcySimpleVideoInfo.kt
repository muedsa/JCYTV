package com.muedsa.jcytv.model

data class JcySimpleVideoInfo(
    val title: String,
    val subTitle: String = "",
    val detailPagePath: String = "",
    val imageUrl: String = "",
) {
    val id: Long by lazy {
        detailPagePath.substringAfter("/index.php/vod/detail/id/").removeSuffix(".html").toLong()
    }
}