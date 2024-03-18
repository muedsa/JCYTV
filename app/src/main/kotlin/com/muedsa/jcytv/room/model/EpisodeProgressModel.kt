package com.muedsa.jcytv.room.model

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "episode_progress", primaryKeys = ["aid", "title_hash"])
data class EpisodeProgressModel(
    @ColumnInfo(name = "aid") val aid: Long,
    @ColumnInfo(name = "title_hash") val titleHash: Int,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "progress", defaultValue = "0") var progress: Long,
    @ColumnInfo(name = "duration", defaultValue = "0") var duration: Long,
    @ColumnInfo(
        name = "update_at",
        defaultValue = "(CURRENT_TIMESTAMP)",
        index = true
    ) var updateAt: Long = 0
) {
    companion object {
        val Empty = EpisodeProgressModel(0L, 0, "", 0, 0, 0)
    }
}
