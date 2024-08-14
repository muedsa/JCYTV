package com.muedsa.jcytv.util

import com.google.common.net.HttpHeaders
import com.muedsa.jcytv.model.JcyRawPlaySource
import com.muedsa.uitl.decodeBase64
import com.muedsa.uitl.decryptAES128CBCPKCS7
import com.muedsa.uitl.encryptRC4
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.net.URI
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

object JcyPlaySourceTool {

    const val CHROME_USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/127.0.0.0 Safari/537.36"

    val DECRYPT_DEFAULT: (String) -> String = { key: String -> key }

    val DECRYPT_NOT_SUPPORT: (String) -> String =
        { _: String -> throw IllegalStateException("不支持的播放源") }

    private val SILISILI_ENCRYPTED_URL_REGEX = Regex("\"url\":\"([A-Za-z0-9+/=\\\\]*?)\"")
    private val SILISILI_UID_REGEX = Regex("\"uid\":\"([A-Za-z0-9+/=\\\\]*?)\"")

    val DECRYPT_SILISILI: (String) -> String = { key: String ->
        val doc: Document =
            Jsoup.connect("https://play.silisili.top/player/ec.php?code=ttnb&if=1&url=$key")
                .header(HttpHeaders.REFERER, JcyConst.HOME_URL)
                .header(HttpHeaders.USER_AGENT, CHROME_USER_AGENT)
                .get()
        val bodyHtml = doc.body().html()
        val urlMatchResult = SILISILI_ENCRYPTED_URL_REGEX.find(bodyHtml)
        val uidMatchResult = SILISILI_UID_REGEX.find(bodyHtml)
        val encryptedUrl = urlMatchResult!!.groupValues[1].replace("\\/", "/")
        val uid = uidMatchResult!!.groupValues[1]
        encryptedUrl.decodeBase64().decryptAES128CBCPKCS7("2890${uid}tB959C", "2F131BE91247866E")
            .toString(Charsets.UTF_8)
    }

    private val DILIDILI_ENCRYPTED_URL_REGEX = Regex("\"url\": \"([A-Za-z0-9+/=\\\\]*?)\"")

    val DECRYPT_DILIDILI: (String) -> String = { key: String ->
        val doc: Document = Jsoup.connect("https://play.dilidili.ink/player/analysis.php?v=$key")
            .header(HttpHeaders.REFERER, JcyConst.HOME_URL)
            .header(HttpHeaders.USER_AGENT, CHROME_USER_AGENT)
            .get()
        val bodyHtml = doc.body().html()
        val urlMatchResult = DILIDILI_ENCRYPTED_URL_REGEX.find(bodyHtml)
        val encryptedUrl = urlMatchResult!!.groupValues[1].replace("\\/", "/")
        URLDecoder.decode(
            encryptedUrl.decodeBase64()
                .encryptRC4("202205051426239465".toByteArray())
                .decodeToString(),
            StandardCharsets.UTF_8.name()
        )
    }

    val DECRYPT_LIBILIBI: (String) -> String = { key: String ->
        if (key.endsWith(".m3u8", true)
            || key.endsWith(".mp4", true)
            || key.endsWith(".flv", true)
        ) key
        else TODO("NOT_SUPPORT")
    }

    val PLAYER_SITE_MAP: Map<String, (String) -> String> = mapOf(
        "NBY" to DECRYPT_DILIDILI, // ✅囧次元N
        "ttnb" to DECRYPT_DILIDILI, // https://v.dilidili.ink/player/?url=
        "lzm3u8" to DECRYPT_DILIDILI, // ✅囧次元Z
        "snm3u8" to DECRYPT_DILIDILI, // ✅囧次元O
        "cycp" to DECRYPT_DEFAULT, // ?
        "ffm3u8" to DECRYPT_DILIDILI, // ✅囧次元A
        "SLNB" to DECRYPT_DEFAULT, // ?
        "dplayer" to DECRYPT_DEFAULT, //  dplayer
        "videojs" to DECRYPT_NOT_SUPPORT, // 不支持videojs
        "iva" to DECRYPT_DEFAULT,
        "iframe" to DECRYPT_NOT_SUPPORT, // 不支持iframe
        "link" to DECRYPT_NOT_SUPPORT, // 不支持link
        "swf" to DECRYPT_NOT_SUPPORT, // 不支持swf
        "flv" to DECRYPT_DEFAULT,
        "ACG" to DECRYPT_SILISILI, // https://play.silisili.top/player/ec.php?code=ttnb&if=1&url=
        "languang" to DECRYPT_DEFAULT, // TODO APP线路 https://player.123tv.icu/player/ec.php?code=yunq&if=1&url=
    )

    fun getAbsoluteUrl(path: String): String {
        return if (path.startsWith("http://") || path.startsWith("https://")) {
            path
        } else {
            URI.create(JcyConst.HOME_URL).resolve(path).toString()
        }
    }

    private val RAW_PLAY_SOURCE_URL_REGEX = Regex("\"url\":\"([A-Za-z0-9%]*?)\"")
    private val RAW_PLAY_SOURCE_URL_NEXT_REGEX = Regex("\"url_next\":\"([A-Za-z0-9%]*?)\"")
    private val RAW_PLAY_SOURCE_FROM_REGEX = Regex("\"from\":\"([A-Za-z0-9]*?)\"")

    fun getRawPlaySource(url: String): JcyRawPlaySource {
        val doc: Document = Jsoup.connect(url)
            .header(HttpHeaders.REFERER, JcyConst.HOME_URL)
            .header(HttpHeaders.USER_AGENT, CHROME_USER_AGENT)
            .get()
        val bodyHtml = doc.body().html()

        return JcyRawPlaySource(
            url = RAW_PLAY_SOURCE_URL_REGEX.find(bodyHtml)!!.groupValues[1],
            urlNext = RAW_PLAY_SOURCE_URL_NEXT_REGEX.find(bodyHtml)!!.groupValues[1],
            from = RAW_PLAY_SOURCE_FROM_REGEX.find(bodyHtml)!!.groupValues[1]
        )
    }

    fun getRealPlayUrl(rawPlaySource: JcyRawPlaySource): String {
        val decodedUrl = URLDecoder.decode(rawPlaySource.url, StandardCharsets.UTF_8.name())
        val decrypt = PLAYER_SITE_MAP[rawPlaySource.from]
        return decrypt?.invoke(decodedUrl) ?: decodedUrl
    }
}