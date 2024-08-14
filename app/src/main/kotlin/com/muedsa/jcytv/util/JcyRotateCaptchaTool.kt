package com.muedsa.jcytv.util

import com.google.common.net.HttpHeaders
import com.muedsa.uitl.encodeBase64
import okhttp3.Headers
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.nodes.Element
import java.net.HttpCookie

object JcyRotateCaptchaTool {

    const val CAPTCHA_CHECK_URL = "https://9ciyuan.com"

    const val HTML_KEYWORD = "/_guard/html.js?js=rotate_html"

    private const val CAPTCHA_IMAGE_URL = "https://9ciyuan.com/_guard/rotate.jpg?t="

    const val COOKIE_GUARD = "guard"
    const val COOKIE_GUARD_RESULT = "guardret"
    const val COOKIE_GUARD_OK = "guardok"

    private const val MAGIC_STRING = "qweabcPTNo2n3Ev5"

    private val client: OkHttpClient = OkHttpClient.Builder().build()

    fun checkIfNeedValidateCaptcha(head: Element): Boolean {
        return head.html().contains(HTML_KEYWORD)
    }

    fun buildCaptchaImageUrl(): String {
        val t = System.currentTimeMillis()
        return "$CAPTCHA_IMAGE_URL$t"
    }

    fun getGuardRet(deg: Float): String {
        val degCharArr = deg.toString().toCharArray()
        var output = ""
        for ((index, c) in degCharArr.withIndex()) {
            val charCode = c.code xor MAGIC_STRING[index].code
            output += charCode.toChar()
        }
        return output.toByteArray(Charsets.UTF_8).encodeBase64()
    }

    fun getGuardOk(guard: String, deg: Float): String? {
        val req = Request.Builder()
            .url(CAPTCHA_CHECK_URL)
            .header(HttpHeaders.REFERER, CAPTCHA_CHECK_URL)
            .header(HttpHeaders.USER_AGENT, JcyConst.CHROME_USER_AGENT)
            .header(HttpHeaders.COOKIE, "$COOKIE_GUARD=$guard; $COOKIE_GUARD_RESULT=${getGuardRet(deg)}")
            .get()
            .build()
        val resp = client.newCall(req).execute()
        return getSetCookieValueFromHeaders(resp.headers, COOKIE_GUARD_OK)
    }

    fun getSetCookieValueFromHeaders(headers: Headers, name: String): String? {
        val setCookieList = headers.values(HttpHeaders.SET_COOKIE)
        return setCookieList.find { it.startsWith("$name=") }?.also {
            val cookieList = HttpCookie.parse(it)
            val cookie = cookieList.firstOrNull { c -> c.name == name }
            return cookie?.value
        }
    }
}

