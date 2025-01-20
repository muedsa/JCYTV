package com.muedsa.jcytv.util

import com.muedsa.jcytv.model.JcyRankList
import com.muedsa.jcytv.model.JcyRankVideoInfo
import com.muedsa.jcytv.model.JcySimpleVideoInfo
import com.muedsa.jcytv.model.JcyVideoDetail
import com.muedsa.jcytv.model.JcyVideoRow
import org.jsoup.nodes.Element

object JcyHtmlParserTool {

    fun parseHomeVideoRows(body: Element): List<JcyVideoRow> {
        val rows = mutableListOf<JcyVideoRow>()
        val weekRows = mutableListOf<JcyVideoRow>()
        body.select(".main .module").forEach {
            if (!it.hasClass("module-shadow")) {
                parseModuleEl(it).forEach {
                    if (it.list.isNotEmpty()) {
                        if (it.title.startsWith("追剧周表")) {
                            weekRows.add(it)
                        } else {
                            rows.add(it)
                        }
                    }
                }
            }
        }
        rows.addAll(weekRows)
        return rows
    }

    fun parseModuleEl(moduleEl: Element): List<JcyVideoRow> {
        val moduleHeadingEl = moduleEl.selectFirst(".module-heading")
        return if (moduleHeadingEl != null) {
            parseHasHeadModuleEl(moduleEl = moduleEl, moduleHeadingEl = moduleHeadingEl)
        } else {
            parseNoHeadModuleEl(
                rowTitle = "XD",
                tabs = null,
                moduleEl = moduleEl
            )
        }
    }

    fun parseHasHeadModuleEl(moduleEl: Element, moduleHeadingEl: Element): List<JcyVideoRow> {
        val titleEl = moduleHeadingEl.selectFirst(".module-title")
        val rowTitle = if (titleEl != null) {
            titleEl.selectFirst("a")?.text()
                ?.removeSuffix(titleEl.selectFirst("a .module-title-en")?.text() ?: "")?.trim()
                ?: titleEl.text().trim()
        } else ""
        val tabs = moduleHeadingEl.select(".module-tab .module-tab-item").map { it.text().trim() }
        return parseNoHeadModuleEl(
            rowTitle = rowTitle,
            tabs = tabs,
            moduleEl = moduleEl,
        )
    }

    fun parseNoHeadModuleEl(
        rowTitle: String,
        tabs: List<String>?,
        moduleEl: Element,
    ): List<JcyVideoRow> {
        val mainEls = moduleEl.select(".module-main")
        return mainEls.mapIndexed { index, mainEl ->
            JcyVideoRow(
                title = if (mainEls.size > 1) "$rowTitle ${tabs?.getOrElse(index) { "$index" } ?: "$index"}" else rowTitle,
                list = mainEl.select(".module-items .module-item").mapNotNull { itemEl ->
                    if (itemEl.normalName() == "a") {
                        JcySimpleVideoInfo(
                            title = itemEl.selectFirst(".module-poster-item-info .module-poster-item-title")
                                ?.text()?.trim() ?: "",
                            subTitle = itemEl.selectFirst(".module-item-cover .module-item-note")
                                ?.text()?.trim() ?: "",
                            detailPagePath = itemEl.attr("href"),
                            imageUrl = itemEl.selectFirst(".module-item-cover .module-item-pic img")
                                ?.absUrl("data-original") ?: "",
                        )
                    } else if (itemEl.hasClass("module-card-item")){
                        JcySimpleVideoInfo(
                            title = itemEl.selectFirst(".module-card-item-info .module-card-item-title")
                                ?.text()?.trim() ?: "",
                            subTitle = itemEl.selectFirst(".module-item-cover .module-item-note")
                                ?.text()?.trim() ?: "",
                            detailPagePath = itemEl.selectFirst("a.module-card-item-poster")!!.attr("href"),
                            imageUrl = itemEl.selectFirst(".module-item-cover .module-item-pic img")
                                ?.absUrl("data-original") ?: "",
                        )
                    } else null
                }
            )
        }
    }

    fun parseRankList(body: Element): List<JcyRankList> {
        return body.select(".main .module.module-shadow .module-main .module-paper-item.module-item").map {
            val title = it.selectFirst(".module-paper-item-header .module-paper-item-title")!!.text()
            val list = it.select(".module-paper-item-main >a").map { aEl ->
                JcyRankVideoInfo(
                    title = aEl.selectFirst(".module-paper-item-info >span.module-paper-item-infotitle")!!.text().trim(),
                    subTitle = aEl.selectFirst(".module-paper-item-info >p")!!.text().trim(),
                    detailPagePath = aEl!!.attr("href"),
                    index = aEl.selectFirst(".module-paper-item-num")!!.text().trim().toInt()
                )
            }
            JcyRankList(title = title, list = list)
        }
    }

    fun parseVideoDetail(body: Element): JcyVideoDetail {
        val moduleInfoEl = body.selectFirst(".page .main .content .module.module-info")!!
        val moduleEls = body.select(".page .main .content .module").filter { !it.hasClass("module-info") }
        val playList = moduleEls.find { it.selectFirst(".player-heading") != null }?.let { playModuleEl ->
            val sources = playModuleEl.select(".player-heading .module-tab .module-tab-items-box .module-tab-item").map {
                it.attr("data-dropdown-value")
            }
            playModuleEl.select(".module-list.tab-list").mapIndexed { index, item ->
                Pair(
                    sources.getOrElse(index) { "$index" },
                    item.select(".module-play-list-link").map { linkEl ->
                        linkEl.text() to linkEl.absUrl("href")
                    }
                )
            }
        } ?: emptyList()
        return JcyVideoDetail(
            detailPagePath = body.baseUri(),
            title = moduleInfoEl.selectFirst(".module-main .module-info-main .module-info-heading h1")
                ?.text()?.trim() ?: "",
            tags = moduleInfoEl.selectFirst(".module-main .module-info-main .module-info-heading .module-info-tag")
                ?.text()?.trim() ?: "",
            description = moduleInfoEl.selectFirst(".module-main .module-info-main .module-info-items")
                ?.children()?.joinToString("\n") { it.text().trim() } ?: "",
            imageUrl = moduleInfoEl.selectFirst(".module-poster-bg .module-item-cover .module-item-pic img")
                ?.absUrl("data-original") ?: "",
            playList = playList
        )
    }
}