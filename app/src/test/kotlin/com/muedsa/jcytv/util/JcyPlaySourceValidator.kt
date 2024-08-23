package com.muedsa.jcytv.util

import org.junit.Test
import java.net.URL

class JcyPlaySourceValidator {

    @Test
    fun valid_SLNB() {
        // 囧简体
        val text = getHttpContent("https://www.9ciyuan.com/static/player/SLNB.js")
        check(text.indexOf("https://play.dilidili.ink/player/?url=") >= -1)
    }

    @Test
    fun valid_dm295() {
        // 囧囧囧
        val text = getHttpContent("https://www.9ciyuan.com/static/player/dm295.js")
        check(text.indexOf("https://jx.dilidili.ink/player/?url=") >= -1)
    }

    @Test
    fun valid_ffm3u8() {
        // 囧次元A
        val text = getHttpContent("https://www.9ciyuan.com/static/player/ffm3u8.js")
        check(text.indexOf("https://play.dilidili.ink/player/?url=") >= -1)
    }

    @Test
    fun valid_bfzym3u8() {
        // 囧次元B
        val text = getHttpContent("https://www.9ciyuan.com/static/player/bfzym3u8.js")
        check(text.indexOf("https://jx.dilidili.ink/player/?url=") >= -1)
    }

    @Test
    fun valid_lzm3u8() {
        // 囧次元Z
        val text = getHttpContent("https://www.9ciyuan.com/static/player/lzm3u8.js")
        check(text.indexOf("https://play.dilidili.ink/player/?url=") >= -1)
    }

    @Test
    fun valid_NBY() {
        // 囧次元N
        val text = getHttpContent("https://www.9ciyuan.com/static/player/NBY.js")
        check(text.indexOf("https://play.dilidili.ink/player/?url=") >= -1)
    }

    @Test
    fun valid_ttnb() {
        // 囧次狼
        val text = getHttpContent("https://www.9ciyuan.com/static/player/ttnb.js")
        check(text.indexOf("https://play.dilidili.ink/player/?url=") >= -1)
    }

    @Test
    fun valid_snm3u8() {
        // 囧次元O
        val text = getHttpContent("https://www.9ciyuan.com/static/player/snm3u8.js")
        check(text.indexOf("https://jx.dilidili.ink/player/?url=") >= -1)
    }

    @Test
    fun valid_1080zyk() {
        // 囧次元Y
        val text = getHttpContent("https://www.9ciyuan.com/static/player/1080zyk.js")
        check(text.indexOf("https://play.dilidili.ink/player/?url=") >= -1)
    }

    @Test
    fun jx_dilidili_iframe_valid() {
        val text = getHttpContent("https://jx.dilidili.ink/player/?url=233")
        check(text.indexOf("<iframe src=\"analysis.php?") >= -1)
    }

    @Test
    fun play_dilidili_iframe_valid() {
        val text = getHttpContent("https://play.dilidili.ink/player/?url=233")
        check(text.indexOf("<iframe src=\"analysis.php?") >= -1)
    }

    @Test
    fun dilidili_js_keyword_valid() {
        val text = getHttpContent("https://play.dilidili.ink/mizhiplayerapi/js/setting.js")
        check(text.indexOf("YKQ.play(rc4(config.url,'202205051426239465',1));") >= -1)
    }

    // @Test
    fun libilibi_js_keyword_valid() {
        val text = getHttpContent("https://play.libilibi.top/js/setting.js")
        check(text.indexOf("\$.post(\"\", {") >= -1)
    }

    private fun getHttpContent(url: String): String = URL(url).openConnection()
        .getInputStream().use {
            it.readAllBytes().decodeToString()
        }
}