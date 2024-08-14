package com.muedsa.jcytv.util

import com.google.common.net.HttpHeaders
import com.muedsa.jcytv.model.JcyVideoRow
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.junit.Test

class JcyHtmlParserToolTest {

    @Test
    fun getHomeVideoRows_test() {
        val doc: Document = Jsoup.connect(JcyConst.HOME_URL)
            .header(HttpHeaders.REFERER, JcyConst.HOME_URL)
            .header(HttpHeaders.USER_AGENT, JcyConst.CHROME_USER_AGENT)
            .get()
        val rows: List<JcyVideoRow> = JcyHtmlParserTool.parseHomVideoRows(doc.body())
        rows.forEach {
            println("# ${it.title}")
            it.list.forEach { e ->
                println("${e.title} ${e.subTitle} ${e.detailPagePath} ${e.imageUrl}")
            }
        }
    }

//
//    @Test
//    fun searchVideo_test() {
//        val list = JcyHtmlTool.searchVideo("1")
//        list.forEach {
//            println("${it.title} ${it.subTitle} ${it.detailPagePath} ${it.imageUrl}")
//        }
//    }
//
//    @Test
//    fun rankList_test() {
//        val list = JcyHtmlTool.rankList()
//        list.forEach {
//            println("# ${it.first}")
//            it.second.forEach { anime ->
//                println("${anime.id} ${anime.hotNum} ${anime.title} ${anime.subTitle} ${anime.detailPagePath} ${anime.imageUrl} ${anime.index}")
//            }
//        }
//    }
//
//    @Test
//    fun catalog_test() {
//        val list = JcyHtmlTool.catalog(
//            mapOf(
//                "id" to "20",
//                "page" to "2",
//                "area" to "日本",
//            )
//        )
//        list.forEach {
//            println("${it.title} ${it.subTitle} ${it.detailPagePath} ${it.imageUrl}")
//        }
//    }
//
//    @Test
//    fun getVideoDetailByUrl_test() {
//        val url = "https://www.9ciyuan.com/index.php/vod/detail/id/3010.html"
//        val detail = JcyHtmlTool.getVideoDetailByUrl(url)
//        println(detail.id)
//        println(detail.detailPagePath)
//        println(detail.title)
//        println(detail.status)
//        println(detail.description)
//        println(detail.imageUrl)
//        detail.playList.forEach {
//            println("# ${it.first}")
//            it.second.forEach { e ->
//                println("${e.first} ${e.second}")
//            }
//        }
//    }
}