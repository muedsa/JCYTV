package com.muedsa.jcytv

import android.app.Application
import com.muedsa.uitl.AppUtil
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class MainApp : Application() {

    override fun onCreate() {
        super.onCreate()
        if (AppUtil.debuggable(applicationContext)) {
            Timber.plant(Timber.DebugTree())
        }
    }
}