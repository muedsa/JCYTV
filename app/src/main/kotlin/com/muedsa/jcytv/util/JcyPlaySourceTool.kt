package com.muedsa.jcytv.util

import com.muedsa.jcytv.model.PlayerAAAA
import com.muedsa.uitl.LenientJson
import com.muedsa.uitl.decodeBase64ToStr
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import timber.log.Timber
import java.net.URI
import java.net.URLDecoder

object JcyPlaySourceTool {

    private val JS_URL_REGEX = Regex("\"url\":\\s*\"(.*?)\"")
    val PLAYER_INFO_REGEX =
        "<script type=\"text/javascript\">var player_aaaa=(\\{.*?\\})</script>".toRegex()

    val DECRYPT_JCY_PLAYER: (PlayerAAAA) -> String = {
        val body = Jsoup.connect("${JcyConst.BASE_PATH}/player/?code=print&url=${it.url}")
            .feignChrome(referrer = JcyConst.HOME_URL)
            .get()
            .body()
        val result = JS_URL_REGEX.find(body.html())
        result?.groups[1]?.value?.decodeBase64ToStr() ?: throw RuntimeException("解析地址失败")
    }

    val PLAYER_SITE_MAP: Map<String, (PlayerAAAA) -> String> = mapOf(
        "aiciyuan" to DECRYPT_JCY_PLAYER, // 囧次元自建
        "bfzym3u8" to DECRYPT_JCY_PLAYER, // 囧次元1
        "lzm3u8" to DECRYPT_JCY_PLAYER, // 囧次元2
        "ffm3u8" to DECRYPT_JCY_PLAYER, // 囧次元3
        "1080zyk" to DECRYPT_JCY_PLAYER, // 囧次元4
    )

    fun getAbsoluteUrl(path: String): String {
        return if (path.startsWith("http://") || path.startsWith("https://")) {
            path
        } else {
            URI.create(JcyConst.HOME_URL).resolve(path).toString()
        }
    }

    fun getPlayerAAAA(url: String): PlayerAAAA {
        val doc: Document = Jsoup.connect(url)
            .feignChrome(referrer = JcyConst.HOME_URL)
            .get()
        val bodyHtml = doc.body().html()
        val result = PLAYER_INFO_REGEX.find(bodyHtml)
        val playerAAAAJson = result?.groups?.get(1)?.value
        if (result == null || playerAAAAJson == null) {
            Timber.e("get player_aaaa error: $url")
            throw RuntimeException("解析地址失败")
        }
        Timber.i("player_aaaa = $playerAAAAJson")
        var playerAAAA = LenientJson.decodeFromString<PlayerAAAA>(playerAAAAJson)
        if (playerAAAA.encrypt == 1) {
            playerAAAA = playerAAAA.copy(
                url = decodeURIComponent(playerAAAA.url),
                urlNext = decodeURIComponent(playerAAAA.urlNext),
            )
        }
        return playerAAAA
    }

    fun getRealPlayUrl(playerAAAA: PlayerAAAA): String {
        return if (playerAAAA.url.endsWith(".m3u8") || playerAAAA.url.endsWith(".mp4")) {
            playerAAAA.url
        } else {
            val decrypt = PLAYER_SITE_MAP[playerAAAA.from] ?: DECRYPT_JCY_PLAYER
            decrypt.invoke(playerAAAA)
        }
    }

    val UNICODE_HEX_REGEX = "%u[A-Z0-9]+".toRegex()

    fun decodeURIComponent(text: String): String {
        var fixedText = text
        val results = UNICODE_HEX_REGEX.findAll(text)
        results.forEach {
            val chunkText = it.groups[0]!!.value
            fixedText = fixedText.replace(chunkText, parseUnicodeString(chunkText))
        }
        return URLDecoder.decode(fixedText, Charsets.UTF_8.name())
    }

    fun parseUnicodeString(hexString: String): String {
        val stringBuilder = StringBuilder()
        for (i in 0..< hexString.length step 6) {
            val hexCode = hexString.substring(i + 2, i + 6)
            val codePoint = Integer.parseInt(hexCode, 16)
            stringBuilder.append(Character.toChars(codePoint))
        }
        return stringBuilder.toString()
    }
}