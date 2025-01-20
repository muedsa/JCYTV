package com.muedsa.jcytv.util

import com.muedsa.jcytv.model.JcyVideoRow
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.junit.Test

class JcyHtmlParserToolTest {

    @Test
    fun getHomeVideoRows_test() {
        val doc: Document = Jsoup.connect(JcyConst.HOME_URL)
            .feignChrome()
            .get()
        val rows: List<JcyVideoRow> = JcyHtmlParserTool.parseHomeVideoRows(doc.body())
        check(rows.isNotEmpty())
        rows.forEach {
            println("# ${it.title}")
            it.list.forEach { e ->
                println("${e.title} ${e.subTitle} ${e.detailPagePath} ${e.imageUrl}")
            }
        }
    }

    @Test
    fun parseRankList_test() {
        val doc: Document = Jsoup.connect(JcyConst.HOME_URL)
            .feignChrome()
            .get()
        val ranks = JcyHtmlParserTool.parseRankList(doc.body())
        check(ranks.isNotEmpty())
        ranks.forEach {
            println("# ${it.title}")
            it.list.forEach { anime ->
                println("${anime.id} ${anime.title} ${anime.subTitle} ${anime.detailPagePath} ${anime.index}")
            }
        }
    }

    @Test
    fun parseVideoRow_searchVideos_test() {
        val doc: Document = Jsoup.connect(JcyConst.SEARCH_URL.replace("{query}", "葬送的芙莉莲"))
            .feignChrome()
            .get()
        val moduleEl = doc.body().selectFirst(".main .content .module")!!
        val rows = JcyHtmlParserTool.parseModuleEl(moduleEl)
        val list = if (rows.isNotEmpty()) rows[0].list else emptyList()
        check(list.isNotEmpty())
        list.forEach {
            println("${it.title} ${it.subTitle} ${it.detailPagePath} ${it.imageUrl}")
        }
    }

    @Test
    fun parseVideoRow_catalog_test() {
        val query = mapOf(
            "id" to "20",
            "page" to "2",
        ).toSortedMap().map {
            "/${it.key}/${it.value}"
        }.joinToString("")
        val doc: Document = Jsoup.connect(JcyConst.CATALOG_URL.replace("{query}", query))
            .feignChrome()
            .get()
        val moduleMainEl = doc.body().selectFirst(".main .module .module-main.module-page")!!
        val rows = JcyHtmlParserTool.parseNoHeadModuleEl("", null, moduleMainEl)
        val list = if (rows.isNotEmpty()) rows[0].list else emptyList()
        check(list.isNotEmpty())
        list.forEach {
            println("${it.title} ${it.subTitle} ${it.detailPagePath} ${it.imageUrl}")
        }
    }

    @Test
    fun parseVideoDetail_test() {
        val url = JcyConst.DETAIL_URL.replace("{id}", "43358")
        val doc: Document = Jsoup.connect(url)
            .feignChrome()
            .get()

        val detail = JcyHtmlParserTool.parseVideoDetail(doc.body())
        checkNotNull(detail.id)
        checkNotNull(detail.detailPagePath)
        checkNotNull(detail.title)
        checkNotNull(detail.tags)
        checkNotNull(detail.description)
        checkNotNull(detail.imageUrl)
        checkNotNull(detail.playList)
        check(detail.playList.isNotEmpty())
        detail.playList.forEach {
            println("# ${it.first}")
            it.second.forEach { e ->
                println("${e.first} ${e.second}")
            }
        }
    }
}