package com.muedsa.jcytv.util

import com.muedsa.jcytv.model.JcyRankList
import com.muedsa.jcytv.model.JcyRankVideoInfo
import com.muedsa.jcytv.model.JcySimpleVideoInfo
import com.muedsa.jcytv.model.JcyVideoDetail
import com.muedsa.jcytv.model.JcyVideoRow
import org.jsoup.nodes.Element

object JcyHtmlParserTool {

    fun parseHomVideoRows(body: Element): List<JcyVideoRow> {
        val rows = mutableListOf<JcyVideoRow>()
        body.select(".vod-list").forEach {
            rows.add(parseVideoRow(it))
        }
        body.select(".vod-list-tv").forEach {
            rows.add(parseTvVideoRow(it))
        }
        return rows
    }

    fun parseVideoRow(rowEl: Element): JcyVideoRow {
        val title = rowEl.selectFirst("h2")!!.text()
        val list = rowEl.select("ul li")
            .filter { it -> it.selectFirst(".pic") != null }
            .map {
                val imgDiv = it.selectFirst(".pic")!!
                val nameDiv = it.selectFirst(".name")!!
                val a = nameDiv.selectFirst("h3 a")!!
                JcySimpleVideoInfo(
                    title = a.text(),
                    subTitle = nameDiv.selectFirst("p")!!.text(),
                    detailPagePath = a.attr("href"),
                    imageUrl = imgDiv.selectFirst(".img-wrapper")!!.absUrl("data-original")
                )
            }
        return JcyVideoRow(title = title, list = list)
    }

    private fun parseTvVideoRow(rowEl: Element): JcyVideoRow {
        val title = rowEl.selectFirst("h2 a")!!.text()
        val list = rowEl.select("ul li").map {
            if (it.hasClass("ranking-item")) {
                val imgDiv = it.selectFirst(".ranking-item-left")!!
                val nameDiv = it.selectFirst(".ranking-item-info")!!
                JcySimpleVideoInfo(
                    title = nameDiv.selectFirst("h4")!!.text(),
                    subTitle = nameDiv.selectFirst("p")!!.text(),
                    detailPagePath = it.selectFirst("a")!!.attr("href"),
                    imageUrl = imgDiv.selectFirst(".img-wrapper")!!.absUrl("data-original")
                )
            } else {
                val imgDiv = it.selectFirst(".pic")!!
                val nameDiv = it.selectFirst(".name")!!
                val a = nameDiv.selectFirst("h3 a")!!
                JcySimpleVideoInfo(
                    title = a.text(),
                    subTitle = nameDiv.selectFirst("p")!!.text(),
                    detailPagePath = a.attr("href"),
                    imageUrl = imgDiv.selectFirst(".img-wrapper")!!.absUrl("data-original")
                )
            }
        }
        return JcyVideoRow(title = title, list = list)
    }

    fun parseRankList(body: Element): List<JcyRankList> {
        return body.select(".index-ranking").map {
            val title = it.selectFirst("h2")!!.text()
            val list = it.parent()!!.select(".ranking-list").select("li").map { li ->
                val infoDiv = li.selectFirst(".ranking-item-info")!!
                JcyRankVideoInfo(
                    title = infoDiv.selectFirst("h4")!!.text(),
                    subTitle = infoDiv.selectFirst("p")!!.text(),
                    detailPagePath = li.selectFirst("a")!!.attr("href"),
                    imageUrl = li.selectFirst(".img-wrapper")!!.absUrl("img-wrapper"),
                    hotNum = li.selectFirst(".ranking-item-hits")!!.text().toInt(),
                    index = li.selectFirst(".ranking-item-num")!!.text().toInt()
                )
            }
            JcyRankList(title = title, list = list)
        }
    }

    fun parseVideoDetail(body: Element): JcyVideoDetail {
        val aEl = body.selectFirst(".vod-info .info h3 a")!!
        val playListTabRefList = body.select(".playlist-tab ul li").map {
            it.attr("data-target") to it.ownText()
        }
        val playList = playListTabRefList.map {
            it.second to body.select("${it.first} li a").map { a ->
                a.text() to a.attr("href")
            }
        }
        return JcyVideoDetail(
            detailPagePath = aEl.attr("href"),
            title = aEl.text(),
            status = body.selectFirst(".vod-info .info > p > span:contains(状态)")!!.text()
                .replace("状态：", "")
                .trim(),
            description = body.selectFirst(".vod-info .info .text")!!.text()
                .replace("简介：", "")
                .trim(),
            imageUrl = body.selectFirst(".vod-info .pic img")!!.absUrl("data-original"),
            playList = playList
        )
    }
}