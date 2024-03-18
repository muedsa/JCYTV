package com.muedsa.jcytv.model

class JcyVideoDetail(
    val detailPagePath: String,
    val title: String,
    val status: String,
    val description: String,
    val imageUrl: String,
    val playList: List<Pair<String, List<Pair<String, String>>>> = emptyList()
) {
    val id: Long by lazy {
        detailPagePath.substringAfter("/index.php/vod/detail/id/").removeSuffix(".html").toLong()
    }
}