package com.muedsa.jcytv.util

import com.google.common.net.HttpHeaders
import com.muedsa.jcytv.model.JcyVideoRow
import com.muedsa.jcytv.util.JcyPlaySourceTool.CHROME_USER_AGENT
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.junit.Test

class JcyHtmlParserToolTest {

    private fun getGuardOk(): String = ""

    @Test
    fun getHomeVideoRows_test() {
        val doc: Document = Jsoup.connect(JcyConst.HOME_URL)
            .header(HttpHeaders.REFERER, JcyConst.HOME_URL)
            .header(HttpHeaders.USER_AGENT, JcyConst.CHROME_USER_AGENT)
            .cookie(JcyRotateCaptchaTool.COOKIE_GUARD_OK, getGuardOk())
            .get()
        val rows: List<JcyVideoRow> = JcyHtmlParserTool.parseHomVideoRows(doc.body())
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
        val doc: Document = Jsoup.connect(JcyConst.RANK_URL)
            .header(HttpHeaders.REFERER, JcyConst.RANK_URL)
            .header(HttpHeaders.USER_AGENT, JcyConst.CHROME_USER_AGENT)
            .get()
        val ranks = JcyHtmlParserTool.parseRankList(doc.body())
        check(ranks.isNotEmpty())
        ranks.forEach {
            println("# ${it.title}")
            it.list.forEach { anime ->
                println("${anime.id} ${anime.hotNum} ${anime.title} ${anime.subTitle} ${anime.detailPagePath} ${anime.imageUrl} ${anime.index}")
            }
        }
    }

    @Test
    fun parseVideoRow_searchVideos_test() {
        val doc: Document = Jsoup.connect("${JcyConst.SEARCH_URL}1")
            .header(HttpHeaders.REFERER, JcyConst.RANK_URL)
            .header(HttpHeaders.USER_AGENT, JcyConst.CHROME_USER_AGENT)
            .cookie(JcyRotateCaptchaTool.COOKIE_GUARD_OK, getGuardOk())
            .get()
        val vodListEl = doc.body().selectFirst(".vod-list")!!
        val list = JcyHtmlParserTool.parseVideoRow(vodListEl).list
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
            "area" to "日本",
        ).toSortedMap().map {
            "/${it.key}/${it.value}"
        }.joinToString("")
        val doc: Document = Jsoup.connect(JcyConst.CATALOG_URL.replace("{query}", query))
            .header(HttpHeaders.REFERER, JcyConst.RANK_URL)
            .header(HttpHeaders.USER_AGENT, JcyConst.CHROME_USER_AGENT)
            .cookie(JcyRotateCaptchaTool.COOKIE_GUARD_OK, getGuardOk())
            .get()
        val vodListEl = doc.body().selectFirst(".vod-list")!!
        val list = JcyHtmlParserTool.parseVideoRow(vodListEl).list
        check(list.isNotEmpty())
        list.forEach {
            println("${it.title} ${it.subTitle} ${it.detailPagePath} ${it.imageUrl}")
        }
    }

    @Test
    fun parseVideoDetail_test() {
        val url = JcyConst.DETAIL_URL.replace("{id}", "3010")
        val doc: Document = Jsoup.connect(url)
            .header(HttpHeaders.REFERER, url)
            .header(HttpHeaders.USER_AGENT, CHROME_USER_AGENT)
            .cookie(JcyRotateCaptchaTool.COOKIE_GUARD_OK, getGuardOk())
            .get()

        val detail = JcyHtmlParserTool.parseVideoDetail(doc.body())
        checkNotNull(detail.id)
        checkNotNull(detail.detailPagePath)
        checkNotNull(detail.title)
        checkNotNull(detail.status)
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