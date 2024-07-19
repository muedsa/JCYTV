// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.kotlinSerialization) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hiltAndroid) apply false
    alias(libs.plugins.gmsGoogleService) apply false
    alias(libs.plugins.firebaseCrashlytics) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.android.room) apply false
    alias(libs.plugins.android.test) apply false
    alias(libs.plugins.baselineprofile) apply false
}