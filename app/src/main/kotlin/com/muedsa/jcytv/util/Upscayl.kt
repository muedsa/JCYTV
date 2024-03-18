package com.muedsa.jcytv.util

import android.net.Uri

object Upscayl {
    fun url(url: String): String =
        "https://upscayl.muedsa.com/upscayl?model=realesrgan-x4plus-anime&url=${Uri.encode(url)}"
}