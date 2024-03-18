package com.muedsa.compose.tv.widget.player

import android.annotation.SuppressLint
import android.view.Gravity
import android.widget.FrameLayout
import androidx.activity.compose.BackHandler
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.util.EventLogger
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.kuaishou.akdanmaku.DanmakuConfig
import com.kuaishou.akdanmaku.render.SimpleRenderer
import com.kuaishou.akdanmaku.ui.DanmakuPlayer
import com.kuaishou.akdanmaku.ui.DanmakuView

@SuppressLint("OpaqueUnitKey")
@OptIn(UnstableApi::class)
@Composable
fun DanmakuVideoPlayer(
    debug: Boolean = false,
    danmakuConfigSetting: DanmakuConfig.() -> Unit = {},
    danmakuPlayerInit: DanmakuPlayer.() -> Unit = {},
    videoPlayerBuilderSetting: ExoPlayer.Builder.() -> Unit = {},
    videoPlayerInit: ExoPlayer.() -> Unit,
) {

    val context = LocalContext.current

    val playerControlTicker = remember { mutableIntStateOf(0) }

    val danmakuConfig = remember {
        DanmakuConfig().apply(danmakuConfigSetting)
    }

    val danmakuPlayer = remember {
        DanmakuPlayer(SimpleRenderer())
    }

    val exoPlayer = remember {
        ExoPlayer.Builder(context)
            .also(videoPlayerBuilderSetting)
            .build()
            .also {
                if (debug) {
                    it.addAnalyticsListener(EventLogger())
                }
                it.addListener(object : Player.Listener {
                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        if (isPlaying) {
                            danmakuPlayer.seekTo(it.currentPosition)
                            danmakuPlayer.start(danmakuConfig)
                        } else {
                            danmakuPlayer.pause()
                        }
                    }
                })
                it.videoPlayerInit()
            }
    }

    BackHandler(enabled = playerControlTicker.intValue > 0) {
        playerControlTicker.intValue = 0
    }

    DisposableEffect(
        Box(modifier = Modifier.fillMaxSize()) {
            AndroidView(factory = {
                PlayerView(context).apply {
                    hideController()
                    useController = false
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                    player = exoPlayer
                    layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        Gravity.CENTER
                    )
                }
            })
            AndroidView(factory = {
                DanmakuView(context).apply {
                    layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                    )
                }.also {
                    danmakuPlayer.bindView(it)
                    danmakuPlayer.danmakuPlayerInit()
                }
            })
        }

    ) {
        onDispose {
            exoPlayer.release()
            danmakuPlayer.release()
        }
    }

    PlayerControl(debug = debug, player = exoPlayer, state = playerControlTicker)
}