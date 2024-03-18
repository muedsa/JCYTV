package com.muedsa.jcytv.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.muedsa.jcytv.room.dao.EpisodeProgressDao
import com.muedsa.jcytv.room.dao.FavoriteAnimeDao
import com.muedsa.jcytv.room.model.EpisodeProgressModel
import com.muedsa.jcytv.room.model.FavoriteAnimeModel

@Database(entities = [FavoriteAnimeModel::class, EpisodeProgressModel::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteAnimeDao(): FavoriteAnimeDao

    abstract fun episodeProgressDao(): EpisodeProgressDao
}