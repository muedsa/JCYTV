package com.muedsa.jcytv

import android.content.Context
import com.muedsa.jcytv.repository.DataStoreRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DateStoreModule {

    @Singleton
    @Provides
    fun provideDataStoreRepository(@ApplicationContext app: Context) = DataStoreRepo(app)
}