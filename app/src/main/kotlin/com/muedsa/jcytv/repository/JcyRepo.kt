package com.muedsa.jcytv.repository

import com.google.common.net.HttpHeaders
import com.muedsa.jcytv.KEY_CAPTCHA_GUARD_OK
import com.muedsa.jcytv.exception.NeedValidateCaptchaException
import com.muedsa.jcytv.model.JcyRankList
import com.muedsa.jcytv.model.JcySimpleVideoInfo
import com.muedsa.jcytv.model.JcyVideoDetail
import com.muedsa.jcytv.model.JcyVideoRow
import com.muedsa.jcytv.util.JcyConst
import com.muedsa.jcytv.util.JcyHtmlParserTool
import com.muedsa.jcytv.util.JcyPlaySourceTool.CHROME_USER_AGENT
import com.muedsa.jcytv.util.JcyRotateCaptchaTool
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import javax.inject.Inject

class JcyRepo @Inject constructor(dataStoreRepo: DataStoreRepo) {

    private val guardOkFlow: Flow<String> = dataStoreRepo.dataStore.data
        .map { it[KEY_CAPTCHA_GUARD_OK] ?: "" }

    private suspend fun getGuardOk(): String = guardOkFlow.firstOrNull() ?: ""

    suspend fun fetchHomeVideoRows(): List<JcyVideoRow> {
        val doc: Document = Jsoup.connect(JcyConst.HOME_URL)
            .header(HttpHeaders.REFERER, JcyConst.HOME_URL)
            .header(HttpHeaders.USER_AGENT, JcyConst.CHROME_USER_AGENT)
            .cookie(JcyRotateCaptchaTool.COOKIE_GUARD_OK, getGuardOk())
            .get()
        checkIfNeedValidateCaptcha(doc.head())
        return JcyHtmlParserTool.parseHomVideoRows(doc.body())
    }

    suspend fun fetchRankList(): List<JcyRankList> {
        val doc: Document = Jsoup.connect(JcyConst.RANK_URL)
            .header(HttpHeaders.REFERER, JcyConst.RANK_URL)
            .header(HttpHeaders.USER_AGENT, JcyConst.CHROME_USER_AGENT)
            .cookie(JcyRotateCaptchaTool.COOKIE_GUARD_OK, getGuardOk())
            .get()
        checkIfNeedValidateCaptcha(doc.head())
        return JcyHtmlParserTool.parseRankList(doc.body())
    }

    suspend fun searchVideos(query: String): List<JcySimpleVideoInfo> {
        val doc: Document = Jsoup.connect("${JcyConst.SEARCH_URL}$query")
            .header(HttpHeaders.REFERER, JcyConst.RANK_URL)
            .header(HttpHeaders.USER_AGENT, JcyConst.CHROME_USER_AGENT)
            .cookie(JcyRotateCaptchaTool.COOKIE_GUARD_OK, getGuardOk())
            .get()
        checkIfNeedValidateCaptcha(doc.head())
        val vodListEl = doc.body().selectFirst(".vod-list")!!
        return JcyHtmlParserTool.parseVideoRow(vodListEl).list
    }

    suspend fun catalog(queryMap: Map<String, String>): List<JcySimpleVideoInfo> {
        val query = queryMap.toSortedMap().map {
            "/${it.key}/${it.value}"
        }.joinToString("")
        val doc: Document = Jsoup.connect(JcyConst.CATALOG_URL.replace("{query}", query))
            .header(HttpHeaders.REFERER, JcyConst.RANK_URL)
            .header(HttpHeaders.USER_AGENT, JcyConst.CHROME_USER_AGENT)
            .cookie(JcyRotateCaptchaTool.COOKIE_GUARD_OK, getGuardOk())
            .get()
        checkIfNeedValidateCaptcha(doc.head())
        val vodListEl =  doc.body().selectFirst(".vod-list")!!
        return JcyHtmlParserTool.parseVideoRow(vodListEl).list
    }

    suspend fun fetchVideoDetail(id: Long): JcyVideoDetail {
        val url = JcyConst.DETAIL_URL.replace("{id}", id.toString())
        val doc: Document = Jsoup.connect(url)
            .header(HttpHeaders.REFERER, url)
            .header(HttpHeaders.USER_AGENT, CHROME_USER_AGENT)
            .cookie(JcyRotateCaptchaTool.COOKIE_GUARD_OK, getGuardOk())
            .get()
        checkIfNeedValidateCaptcha(doc.head())
        return JcyHtmlParserTool.parseVideoDetail(doc.body())
    }

    private fun checkIfNeedValidateCaptcha(body: Element) {
        if (JcyRotateCaptchaTool.checkIfNeedValidateCaptcha(body))
            throw NeedValidateCaptchaException()
    }
}