package com.muedsa.jcytv.repository

import com.muedsa.jcytv.model.JcyRankList
import com.muedsa.jcytv.model.JcySimpleVideoInfo
import com.muedsa.jcytv.model.JcyVideoDetail
import com.muedsa.jcytv.model.JcyVideoRow
import com.muedsa.jcytv.util.JcyConst
import com.muedsa.jcytv.util.JcyHtmlParserTool
import com.muedsa.jcytv.util.feignChrome
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import javax.inject.Inject

class JcyRepo @Inject constructor() {

    fun fetchHomeVideoRows(): List<JcyVideoRow> {
        val doc = Jsoup.connect(JcyConst.HOME_URL)
            .feignChrome()
            .get()
        return JcyHtmlParserTool.parseHomeVideoRows(doc.body())
    }

    fun fetchRankList(): List<JcyRankList> {
        val doc: Document = Jsoup.connect(JcyConst.HOME_URL)
            .feignChrome()
            .get()
        return JcyHtmlParserTool.parseRankList(doc.body())
    }

    fun searchVideos(query: String): List<JcySimpleVideoInfo> {
        val doc: Document = Jsoup.connect(JcyConst.SEARCH_URL.replace("{query}", query))
            .feignChrome()
            .get()
        val moduleEl = doc.body().selectFirst(".main .content .module")!!
        val rows = JcyHtmlParserTool.parseModuleEl(moduleEl)
        return if (rows.isNotEmpty()) rows[0].list else emptyList()
    }

    fun catalog(queryMap: Map<String, String>): List<JcySimpleVideoInfo> {
        val query = queryMap.toSortedMap().map {
            "/${it.key}/${it.value}"
        }.joinToString("")
        val doc: Document = Jsoup.connect(JcyConst.CATALOG_URL.replace("{query}", query))
            .feignChrome()
            .get()
        val moduleMainEl = doc.body().selectFirst(".main .module .module-main.module-page")!!
        val rows = JcyHtmlParserTool.parseNoHeadModuleEl("", null, moduleMainEl)
        return if (rows.isNotEmpty()) rows[0].list else emptyList()
    }

    fun fetchVideoDetail(id: Long): JcyVideoDetail {
        val url = JcyConst.DETAIL_URL.replace("{id}", id.toString())
        val doc: Document = Jsoup.connect(url)
            .feignChrome()
            .get()
        return JcyHtmlParserTool.parseVideoDetail(doc.body())
    }
}