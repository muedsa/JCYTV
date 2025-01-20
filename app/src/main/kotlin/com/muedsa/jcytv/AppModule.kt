package com.muedsa.jcytv

import android.content.Context
import androidx.room.Room
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.muedsa.jcytv.repository.DataStoreRepo
import com.muedsa.jcytv.repository.JcyRepo
import com.muedsa.jcytv.room.AppDatabase
import com.muedsa.jcytv.service.DanDanPlayApiService
import com.muedsa.uitl.AppUtil
import com.muedsa.uitl.LenientJson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object AppModule {

    @Singleton
    @Provides
    fun provideDataStoreRepository(@ApplicationContext app: Context) = DataStoreRepo(app)

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "JCYTV"
        ).build()
    }

    @Provides
    @Singleton
    fun provideFavoriteAnimeDao(appDatabase: AppDatabase) = appDatabase.favoriteAnimeDao()

    @Provides
    @Singleton
    fun provideEpisodeProgressDao(appDatabase: AppDatabase) = appDatabase.episodeProgressDao()

    @Provides
    @Singleton
    fun provideJcyRepository(): JcyRepo = JcyRepo()

    @Provides
    @Singleton
    fun provideDanDanPlayApiService(@ApplicationContext context: Context): DanDanPlayApiService {
        val client = OkHttpClient.Builder()
            .also {
                if (AppUtil.debuggable(context)) {
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