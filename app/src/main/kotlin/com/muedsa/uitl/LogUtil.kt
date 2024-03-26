package com.muedsa.uitl

import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber

object LogUtil {

    @JvmStatic
    fun d(message: String) {
        Timber.d(message)
    }

    @JvmStatic
    fun d(t: Throwable) {
        Timber.d(t)
    }

    @JvmStatic
    fun d(t: Throwable, message: String) {
        Timber.d(t, message)
    }

    @JvmStatic
    fun fb(message: String) {
        Timber.d(message)
        FirebaseCrashlytics.getInstance().log(message)
    }

    @JvmStatic
    fun fb(t: Throwable) {
        Timber.d(t)
        FirebaseCrashlytics.getInstance().recordException(t)
    }

    @JvmStatic
    fun fb(t: Throwable, message: String) {
        Timber.d(t, message)
        val crashlytics = FirebaseCrashlytics.getInstance()
        crashlytics.log(message)
        crashlytics.recordException(t)
    }
}