package com.muedsa.jcytv.util

import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.junit.Test
import java.security.Security


class JcyPlaySourceToolTest {

    init {
        Security.removeProvider("BC")
        // Confirm that positioning this provider at the end works for your needs!
        Security.addProvider(BouncyCastleProvider())
    }

    @Test
    fun getPlayerAAAA_test() {
        val testUrl = "https://www.9ciyuan.com/index.php/vod/play/id/22/sid/4/nid/1.html"
        val playerAAAA = JcyPlaySourceTool.getPlayerAAAA(testUrl)
        println(playerAAAA.url)
        println(playerAAAA.urlNext)
        println(playerAAAA.from)
    }

    @Test
    fun getRealPlayUrl_test() {
        val url = "https://www.9ciyuan.com/index.php/vod/play/id/22/sid/4/nid/1.html"
        val rawPlaySource = JcyPlaySourceTool.getPlayerAAAA(url)
        val realUrl = JcyPlaySourceTool.getRealPlayUrl(rawPlaySource)
        println(realUrl)
    }

    @Test
    fun getRealPlayUrl_detail_all_test() {
        val url = "https://www.9ciyuan.com/index.php/vod/play/id/22/sid/1/nid/1.html"
        val doc: Document = Jsoup.connect(url)
            .feignChrome(referrer = url)
            .get()
        val detail = JcyHtmlParserTool.parseVideoDetail(doc.body())
        detail.playList.forEach {
            println("# ${it.first}")
            it.second.forEach { e ->
                val playPageUrl = JcyPlaySourceTool.getAbsoluteUrl(e.second)
                val rawPlaySource = JcyPlaySourceTool.getPlayerAAAA(playPageUrl)
                val realUrl = JcyPlaySourceTool.getRealPlayUrl(rawPlaySource)
                check(realUrl.startsWith("http")) { "## ${it.first} ${rawPlaySource.from} -> $realUrl" }
                println("## ${e.first} -> $realUrl")
            }
        }
    }

    @Test
    fun hexStringToUtf8_test() {
        val text = JcyPlaySourceTool.decodeURIComponent("%68%74%74%70%73%3A%2F%2F%63%31%2E%37%62%62%66%66%76%69%70%2E%63%6F%6D%2F%76%69%64%65%6F%2F%62%61%69%78%69%6E%67%67%75%69%7A%75%64%69%65%72%6A%69%2F%u7B2C%30%31%u96C6%2F%69%6E%64%65%78%2E%6D%33%75%38")
        println(text)
    }
}