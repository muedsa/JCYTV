package com.muedsa.jcytv

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.muedsa.jcytv.service.DanDanPlayApiService
import com.muedsa.uitl.LenientJson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object AppModule {

    @Provides
    @Singleton
    fun provideDanDanPlayApiService(): DanDanPlayApiService {
        val client = OkHttpClient.Builder()
            .also {
                if (BuildConfig.DEBUG) {
                    val loggingInterceptor = HttpLoggingInterceptor()
                    loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
                    it.addInterceptor(loggingInterceptor)
                }
            }
            .build()

        return Retrofit.Builder()
            .baseUrl("https://api.dandanplay.net/api/")
            .addConverterFactory(LenientJson.asConverterFactory("application/json".toMediaType()))
            .client(client)
            .build()
            .create(DanDanPlayApiService::class.java)
    }
}