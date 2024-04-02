package com.muedsa.jcytv.util

import org.junit.Test
import java.net.URL

class JcyPlayUrlDecryptValidator {

    @Test
    fun dilidili_js_keyword_valid() {
        val text = getHttpContent("https://v.dilidili.ink/mizhiplayerapi/js/setting.js")
        check(text.indexOf("YKQ.play(rc4(config.url,'202205051426239465',1));") >= -1)
    }

    @Test
    fun libilibi_js_keyword_valid() {
        val text = getHttpContent("https://play.libilibi.top/js/setting.js")
        check(text.indexOf("\$.post(\"\", {") >= -1)
    }

    private fun getHttpContent(url: String): String = URL(url).openConnection()
        .getInputStream().use {
            it.readAllBytes().decodeToString()
        }
}