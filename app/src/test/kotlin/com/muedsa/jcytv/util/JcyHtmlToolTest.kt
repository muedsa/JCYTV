package com.muedsa.jcytv.util

import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.junit.Test
import java.security.Security


class JcyHtmlToolTest {


    init {
        Security.removeProvider("BC")
        // Confirm that positioning this provider at the end works for your needs!
        Security.addProvider(BouncyCastleProvider())
    }

    @Test
    fun getVideoDetailById_test() {
        val id = 3010L
        val detail = JcyHtmlTool.getVideoDetailById(id)
        println(detail.id)
        println(detail.detailPagePath)
        println(detail.title)
        println(detail.status)
        println(detail.description)
        println(detail.imageUrl)
        detail.playList.forEach {
            println("# ${it.first}")
            it.second.forEach { e ->
                println("${e.first} ${e.second}")
            }
        }
    }

    @Test
    fun getVideoDetailByUrl_test() {
        val url = "https://www.9ciyuan.com/index.php/vod/detail/id/3010.html"
        val detail = JcyHtmlTool.getVideoDetailByUrl(url)
        println(detail.id)
        println(detail.detailPagePath)
        println(detail.title)
        println(detail.status)
        println(detail.description)
        println(detail.imageUrl)
        detail.playList.forEach {
            println("# ${it.first}")
            it.second.forEach { e ->
                println("${e.first} ${e.second}")
            }
        }
    }

    @Test
    fun getRawPlaySource_test() {
        val testUrl = "https://www.9ciyuan.com/index.php/vod/play/id/22/sid/4/nid/1.html"
        val (url, urlNext, from) = JcyHtmlTool.getRawPlaySource(testUrl)
        println(url)
        println(urlNext)
        println(from)
    }

    @Test
    fun getRealPlayUrl_test() {
        val url = "https://www.9ciyuan.com/index.php/vod/play/id/22/sid/4/nid/1.html"
        val rawPlaySource = JcyHtmlTool.getRawPlaySource(url)
        val realUrl = JcyHtmlTool.getRealPlayUrl(rawPlaySource)
        println(realUrl)
    }


    @Test
    fun getRealPlayUrl_detail_all_test() {
        val url = "https://www.9ciyuan.com/index.php/vod/play/id/22/sid/1/nid/1.html"
        val detail = JcyHtmlTool.getVideoDetailByUrl(url)
        detail.playList.forEach {
            println("# ${it.first}")
            it.second.forEach { e ->
                val playPageUrl = JcyHtmlTool.getAbsoluteUrl(e.second)
                val rawPlaySource = JcyHtmlTool.getRawPlaySource(playPageUrl)
                val realUrl = JcyHtmlTool.getRealPlayUrl(rawPlaySource)
                check(realUrl.startsWith("http")) { "## ${it.first} ${rawPlaySource.from} -> $realUrl" }
                println("## ${e.first} -> $realUrl")
            }
        }
    }

    @Test
    fun getHomeVideoRows_test() {
        val rows = JcyHtmlTool.getHomeVideoRows()
        rows.forEach {
            println("# ${it.first}")
            it.second.forEach { e ->
                println("${e.title} ${e.subTitle} ${e.detailPagePath} ${e.imageUrl}")
            }
        }
    }

    @Test
    fun searchVideo_test() {
        val list = JcyHtmlTool.searchVideo("1")
        list.forEach {
            println("${it.title} ${it.subTitle} ${it.detailPagePath} ${it.imageUrl}")
        }
    }

    @Test
    fun rankList_test() {
        val list = JcyHtmlTool.rankList()
        list.forEach {
            println("# ${it.first}")
            it.second.forEach { anime ->
                println("${anime.id} ${anime.hotNum} ${anime.title} ${anime.subTitle} ${anime.detailPagePath} ${anime.imageUrl} ${anime.index}")
            }
        }
    }

    @Test
    fun catalog_test() {
        val list = JcyHtmlTool.catalog(
            mapOf(
                "id" to "20",
                "page" to "2",
                "area" to "日本",
            )
        )
        list.forEach {
            println("${it.title} ${it.subTitle} ${it.detailPagePath} ${it.imageUrl}")
        }
    }
}