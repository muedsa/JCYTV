package com.muedsa.jcytv.model

class JcyHomeData(
    val hotList: List<JcySimpleVideoInfo> = emptyList(),
    val newList: List<JcySimpleVideoInfo> = emptyList()
)