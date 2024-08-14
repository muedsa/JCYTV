package com.muedsa.jcytv.util

import org.bouncycastle.jce.provider.BouncyCastleProvider
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

//    @Test
//    fun getRealPlayUrl_detail_all_test() {
//        val url = "https://www.9ciyuan.com/index.php/vod/play/id/22/sid/1/nid/1.html"
//        val detail = JcyHtmlTool.getVideoDetailByUrl(url)
//        detail.playList.forEach {
//            println("# ${it.first}")
//            it.second.forEach { e ->
//                val playPageUrl = JcyHtmlTool.getAbsoluteUrl(e.second)
//                val rawPlaySource = JcyHtmlTool.getRawPlaySource(playPageUrl)
//                val realUrl = JcyHtmlTool.getRealPlayUrl(rawPlaySource)
//                check(realUrl.startsWith("http")) { "## ${it.first} ${rawPlaySource.from} -> $realUrl" }
//                println("## ${e.first} -> $realUrl")
//            }
//        }
//    }
}