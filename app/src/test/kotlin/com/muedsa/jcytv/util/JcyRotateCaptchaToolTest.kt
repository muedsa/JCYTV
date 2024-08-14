package com.muedsa.jcytv.util

import org.junit.Test

class JcyRotateCaptchaToolTest {

    @Test
    fun getGuardRet_test() {
        val e = JcyRotateCaptchaTool.getGuardRet(23.3f)
        assert(e == "Q0RLUg==")
    }
}