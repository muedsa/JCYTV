package com.muedsa.jcytv.util

import com.google.common.net.HttpHeaders
import com.muedsa.jcytv.util.JcyPlaySourceTool.CHROME_USER_AGENT
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
    fun getRawPlaySource_test() {
        val testUrl = "https://www.9ciyuan.com/index.php/vod/play/id/22/sid/4/nid/1.html"
        val (url, urlNext, from) = JcyPlaySourceTool.getRawPlaySource(testUrl)
        println(url)
        println(urlNext)
        println(from)
    }

    @Test
    fun getRealPlayUrl_test() {
        val url = "https://www.9ciyuan.com/index.php/vod/play/id/22/sid/4/nid/1.html"
        val rawPlaySource = JcyPlaySourceTool.getRawPlaySource(url)
        val realUrl = JcyPlaySourceTool.getRealPlayUrl(rawPlaySource)
        println(realUrl)
    }

    @Test
    fun getRealPlayUrl_detail_all_test() {
        val url = "https://www.9ciyuan.com/index.php/vod/play/id/22/sid/1/nid/1.html"
        val doc: Document = Jsoup.connect(url)
            .header(HttpHeaders.REFERER, url)
            .header(HttpHeaders.USER_AGENT, CHROME_USER_AGENT)
            .get()
        val detail = JcyHtmlParserTool.parseVideoDetail(doc.body())
        detail.playList.forEach {
            println("# ${it.first}")
            it.second.forEach { e ->
                val playPageUrl = JcyPlaySourceTool.getAbsoluteUrl(e.second)
                val rawPlaySource = JcyPlaySourceTool.getRawPlaySource(playPageUrl)
                val realUrl = JcyPlaySourceTool.getRealPlayUrl(rawPlaySource)
                check(realUrl.startsWith("http")) { "## ${it.first} ${rawPlaySource.from} -> $realUrl" }
                println("## ${e.first} -> $realUrl")
            }
        }
    }
}