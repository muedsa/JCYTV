package com.muedsa.jcytv.util

import org.jsoup.Connection
import java.net.CookieStore

const val ChromeUserAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36"

fun Connection.feignChrome(referrer: String? = null, cookieStore: CookieStore? = null): Connection {
    return userAgent(ChromeUserAgent)
        .also {
            if (!referrer.isNullOrEmpty()) {
                it.referrer(referrer)
            }
            if (cookieStore != null) {
                it.cookieStore(cookieStore)
            }
        }
        .header("Cache-Control", "no-cache")
        .header("Pragma", "no-cache")
        .header("Priority", "u=0, i")
        .header("Sec-Ch-Ua", "\"Chromium\";v=\"130\", \"Google Chrome\";v=\"130\", \"Not?A_Brand\";v=\"99\"")
        .header("Sec-Ch-Ua-Platform", "\"Windows\"")
        .header("Upgrade-Insecure-Requests", "1")
        .header("Connection", "close")
}