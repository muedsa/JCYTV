package com.muedsa.jcytv.util

object JcyConst {

    private const val BASE_PATH = "https://www.9ciyuan.com" // 目前发现www子域并不会弹验证码, 仅使用主域名会出现旋转图片验证码

    const val HOME_URL = "$BASE_PATH/"

    const val SEARCH_URL = "$BASE_PATH/index.php/vod/search.html?wd="

    const val RANK_URL = "$BASE_PATH/index.php/label/ranking.html"

    const val CATALOG_URL = "$BASE_PATH/index.php/vod/show{query}.html"

    const val DETAIL_URL = "$BASE_PATH/index.php/vod/detail/id/{id}.html"

    const val CHROME_USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/127.0.0.0 Safari/537.36"
}