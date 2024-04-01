package com.muedsa.util

import com.muedsa.uitl.decodeBase64
import com.muedsa.uitl.encryptRC4
import org.junit.Test

class CodecTest {

    @Test
    fun rc4() {
        println("xyBFZVsjOoCXIN0GDoY1hsbw3QKU793PDzxQyEGEO553pBErhFNu1LJQsr9Q+quN9zeqU0XbZSqpuiNjKIV7t+dWpDVoDoEPOtFlJXfIhH7eBF4WdQGa1t30ajer/51mVzF7VzpuZ45POD+La2kUQlm8e1XXqmZQwhzbfYg2oeuMvi4="
            .decodeBase64()
            .encryptRC4("202205051426239465".toByteArray())
            .decodeToString())
    }
}