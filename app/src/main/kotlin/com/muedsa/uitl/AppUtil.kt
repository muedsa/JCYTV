package com.muedsa.uitl

import android.content.Context
import android.content.pm.ApplicationInfo

object AppUtil {

    fun getVersionInfo(context: Context): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            "${packageInfo.versionName}(${packageInfo.longVersionCode})"
        } catch (t: Throwable) {
            ""
        }
    }

    fun debuggable(context: Context): Boolean = (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
}