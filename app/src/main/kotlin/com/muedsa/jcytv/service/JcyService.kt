package com.muedsa.jcytv.service

import org.jsoup.Jsoup

class JcyService {


    fun loadHomeData() {
        val get = Jsoup.connect("https://www.9ciyuan.com/").get()


    }
}