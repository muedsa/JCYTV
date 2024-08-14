package com.muedsa.jcytv.screens.playback

import android.app.Activity
import androidx.annotation.OptIn
import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.kuaishou.akdanmaku.ecs.component.filter.DuplicateMergedFilter
import com.muedsa.compose.tv.useLocalErrorMsgBoxController
import com.muedsa.compose.tv.widget.player.DanmakuVideoPlayer
import com.muedsa.compose.tv.widget.player.mergeDanmaku
import com.muedsa.jcytv.BuildConfig
import com.muedsa.model.LazyType
import com.muedsa.uitl.LogUtil
import kotlinx.coroutines.delay
import kotlin.math.max
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@Composable
@OptIn(UnstableApi::class)
fun PlaybackScreen(
    aid: Long,
    episodeTitle: String,
    mediaUrl: String,
    danEpisodeId: Long = 0,
    playbackViewModel: PlaybackViewModel = hiltViewModel(),
    backListeners: SnapshotStateList<() -> Unit>
) {
    val activity = LocalContext.current as? Activity
    val errorMsgBoxState = useLocalErrorMsgBoxController()

    val danmakuSettingLD by playbackViewModel.danmakuSettingLDSF.collectAsState()
    val danmakuListLD by playbackViewModel.danmakuListLDSF.collectAsState()
    val episodeProgress by playbackViewModel.episodeProgressSF.collectAsState()

    var exoplayerHolder by remember { mutableStateOf<ExoPlayer?>(null) }

    var playerEnd by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = danmakuSettingLD) {
        if (danmakuListLD.type == LazyType.FAILURE) {
            errorMsgBoxState.error(danmakuListLD.error)
        }
    }

    LaunchedEffect(key1 = danmakuListLD) {
        if (danmakuListLD.type == LazyType.FAILURE) {
            errorMsgBoxState.error(danmakuListLD.error)
        }
    }

    LaunchedEffect(key1 = playerEnd) {
        if (playerEnd) {
            if (episodeProgress.aid == aid && exoplayerHolder != null) {
                val exoPlayer = exoplayerHolder!!
                episodeProgress.progress = exoPlayer.duration
                episodeProgress.duration = exoPlayer.duration
                episodeProgress.updateAt = System.currentTimeMillis()
                playbackViewModel.saveEpisodeProgress(episodeProgress)
            }
            errorMsgBoxState.error("播放结束,即将返回")
            delay(3_000)
            activity?.finish()
        }
    }

    LaunchedEffect(key1 = exoplayerHolder) {
        if (exoplayerHolder != null) {
            val exoPlayer = exoplayerHolder!!
            while (exoplayerHolder != null) {
                delay(10_000)
                if (episodeProgress.aid == aid) {
                    episodeProgress.progress = exoPlayer.currentPosition
                    episodeProgress.duration = exoPlayer.duration
                    if (episodeProgress.progress + 5_000 > episodeProgress.duration) {
                        episodeProgress.progress = max(episodeProgress.duration - 5_000, 0)
                    }
                    episodeProgress.updateAt = System.currentTimeMillis()
                    playbackViewModel.saveEpisodeProgress(episodeProgress)
                }
            }
        }
    }

    DisposableEffect(key1 = exoplayerHolder) {
        val listener = {
            if (exoplayerHolder != null) {
                val exoPlayer = exoplayerHolder!!
                if (episodeProgress.aid == aid) {
                    val currentPosition = exoPlayer.currentPosition
                    if (currentPosition > 10_000) {
                        // 观看超过10s才保存进度
                        episodeProgress.progress = currentPosition
                        episodeProgress.duration = exoPlayer.duration
                        episodeProgress.updateAt = System.currentTimeMillis()
                        playbackViewModel.saveEpisodeProgress(episodeProgress)
                    }
                }
            }
        }
        backListeners.add(listener)
        onDispose {
            backListeners.remove(listener)
        }
    }

    if (danmakuListLD.type == LazyType.SUCCESS && danmakuSettingLD.type == LazyType.SUCCESS && episodeProgress.aid == aid) {
        val danmakuSetting = danmakuSettingLD.data!!

        DanmakuVideoPlayer(
            debug = BuildConfig.DEBUG,
            danmakuConfigSetting = {
                textSizeScale = danmakuSetting.danmakuSizeScale / 100f
                alpha = danmakuSetting.danmakuAlpha / 100f
                screenPart = danmakuSetting.danmakuScreenPart / 100f
                dataFilter = listOf(DuplicateMergedFilter().apply { enable = true })
            },
            danmakuPlayerInit = {
                if (!danmakuListLD.data.isNullOrEmpty()) {
                    var list = danmakuListLD.data!!
                    if (danmakuSetting.danmakuMergeEnable) {
                        list = list.mergeDanmaku(5000L, 60000L, 30)
                    }
                    updateData(list)
                }
            }
        ) {
            addListener(object : Player.Listener {

                override fun onPlayerErrorChanged(error: PlaybackException?) {
                    errorMsgBoxState.error(error, SnackbarDuration.Long)
                    error?.let {
                        LogUtil.fb(it, "exoplayer mediaUrl: $mediaUrl")
                    }
                }

                override fun onRenderedFirstFrame() {
                    if (exoplayerHolder == null) {
                        exoplayerHolder = this@DanmakuVideoPlayer
                        if (episodeProgress.progress > 0) {
                            val position =
                                if (episodeProgress.progress == episodeProgress.duration) {
                                    // 如果上次已经播放完成则不跳转 从头播放
                                    0
                                } else if (episodeProgress.duration > 5_000 && episodeProgress.progress > episodeProgress.duration - 5_000) {
                                    // 如果太过接近结束的位置
                                    episodeProgress.duration - 5_000
                                } else {
                                    episodeProgress.progress
                                }
                            if (position > 0) {
                                seekTo(position)
                                val positionStr = position
                                    .toDuration(DurationUnit.MILLISECONDS)
                                    .toComponents { hours, minutes, seconds, _ ->
                                        String.format(
                                            locale = java.util.Locale.getDefault(),
                                            "%02d:%02d:%02d",
                                            hours,
                                            minutes,
                                            seconds,
                                        )
                                    }
                                errorMsgBoxState.error("跳转到上次播放位置: $positionStr")
                            }
                        }
                    }
                }

                override fun onPlaybackStateChanged(playbackState: Int) {
                    val ended = playbackState == Player.STATE_ENDED
                    if (playerEnd != ended) {
                        playerEnd = ended
                    }
                }
            })
            playWhenReady = true
            val mediaItemBuilder = MediaItem.Builder().setUri(mediaUrl)
            if (mediaUrl.endsWith(".m3u8", ignoreCase = true)) {
                mediaItemBuilder.setMimeType(MimeTypes.APPLICATION_M3U8)
            }
            setMediaItem(mediaItemBuilder.build())
            prepare()
        }
    }

    if (danmakuSettingLD.type == LazyType.SUCCESS) {
        val danmakuSetting = danmakuSettingLD.data!!
        LaunchedEffect(key1 = danEpisodeId) {
            playbackViewModel.loadDanmakuList(
                if (danmakuSetting.danmakuEnable)
                    danEpisodeId
                else 0
            )
        }
    }

    LaunchedEffect(key1 = aid, key2 = episodeTitle) {
        playbackViewModel.loadEpisodeProgress(aid, episodeTitle)
    }
}