package com.muedsa.jcytv

import android.content.Context
import androidx.room.Room
import com.muedsa.jcytv.room.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseModule {

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
}