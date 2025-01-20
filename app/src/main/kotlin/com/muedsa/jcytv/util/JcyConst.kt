package com.muedsa.jcytv.util

object JcyConst {

    const val BASE_PATH = "https://www.9ciyuan.com" // 目前发现www子域并不会弹验证码, 仅使用主域名会出现旋转图片验证码

    const val HOME_URL = "$BASE_PATH/"

    const val SEARCH_URL = "$BASE_PATH/index.php/vod/search/wd/{query}.html"

    const val CATALOG_URL = "$BASE_PATH/index.php/vod/show{query}.html"

    const val DETAIL_URL = "$BASE_PATH/index.php/vod/detail/id/{id}.html"
}