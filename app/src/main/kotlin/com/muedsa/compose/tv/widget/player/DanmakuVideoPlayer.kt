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
import com.kuaishou.akdanmaku.data.DanmakuItemData
import com.kuaishou.akdanmaku.render.SimpleRenderer
import com.kuaishou.akdanmaku.ui.DanmakuPlayer
import com.kuaishou.akdanmaku.ui.DanmakuView
import kotlin.random.Random

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

fun List<DanmakuItemData>.mergeDanmaku(
    nearMs: Long,
    maxMs: Long,
    mergedSize: Int,
): List<DanmakuItemData> {
    val rollingCache: MutableMap<String, SimilarDanmakuItemData> = mutableMapOf()
    val fixedCache: MutableMap<String, SimilarDanmakuItemData> = mutableMapOf()
    val result = mutableListOf<DanmakuItemData>()
    this.sorted().forEach {
        if (it.mode == DanmakuItemData.DANMAKU_MODE_ROLLING) {
            mergedDanmaku(rollingCache, it, result, nearMs, maxMs, mergedSize)
        } else if (it.mode == DanmakuItemData.DANMAKU_MODE_CENTER_TOP
            || it.mode == DanmakuItemData.DANMAKU_MODE_CENTER_BOTTOM) {
            mergedDanmaku(fixedCache, it, result, nearMs, maxMs, mergedSize)
        }
    }
    rollingCache.forEach { (_, v) -> v.complete(result, mergedSize) }
    fixedCache.forEach { (_, v) -> v.complete(result, mergedSize) }
    return result
}

private fun mergedDanmaku(
    cache: MutableMap<String, SimilarDanmakuItemData>,
    item: DanmakuItemData,
    result: MutableList<DanmakuItemData>,
    nearMs: Long,
    maxMs: Long,
    mergedSize: Int,
) {
    if (item.mergedType != DanmakuItemData.MERGED_TYPE_NORMAL) {
        result.add(item)
        return
    }

    val existed = cache[item.content]
    if (existed == null) {
        cache[item.content] = SimilarDanmakuItemData(item.content, item)
    } else if(existed.near(nearMs, maxMs, item.position)) {
        existed.inc(item, result)
    } else {
        existed.complete(result, mergedSize)
        cache[item.content] = SimilarDanmakuItemData(item.content, item)
        result.add(item.copyWith(mergedType = DanmakuItemData.MERGED_TYPE_ORIGINAL))
    }
}

class SimilarDanmakuItemData(
    val content: String,
    private val first: DanmakuItemData,
) {
    private var last: DanmakuItemData = first
    private var number: Int = 1

    fun near(nearMs: Long, maxMs: Long, pos: Long): Boolean =
        pos - last.position <= nearMs && pos - first.position <= maxMs

    fun inc(item: DanmakuItemData, result: MutableList<DanmakuItemData>) {
        last = item
        number += 1
        result.add(item.copyWith(mergedType = DanmakuItemData.MERGED_TYPE_ORIGINAL))
    }

    private fun createMerged(mergedSize: Int): DanmakuItemData =
        DanmakuItemData(
            danmakuId = Random.nextLong(),
            position = first.position + 100,
            content = "($number)$content",
            mode = first.mode,
            textSize = mergedSize,
            textColor = first.textColor,
            score = first.score,
            danmakuStyle = DanmakuItemData.DANMAKU_STYLE_NONE,
            mergedType = DanmakuItemData.MERGED_TYPE_MERGED
        )

    fun complete(result: MutableList<DanmakuItemData>, mergedSize: Int) {
        if (number == 1) {
            result.add(first)
        } else {
            result.add(first.copyWith(mergedType = DanmakuItemData.MERGED_TYPE_ORIGINAL))
            result.add(createMerged(mergedSize))
        }
    }
}

fun DanmakuItemData.copyWith(
    danmakuId: Long? = null,
    position: Long? = null,
    content: String? = null,
    mode: Int? = null,
    textSize: Int? = null,
    textColor: Int? = null,
    score: Int? = null,
    danmakuStyle: Int? = null,
    userId: Long? = null,
    mergedType: Int? = null,
): DanmakuItemData =
    DanmakuItemData(
        danmakuId = danmakuId ?: this.danmakuId,
        position = position ?: this.position,
        content = content ?: this.content,
        mode = mode ?: this.mode,
        textSize = textSize ?: this.textSize,
        textColor = textColor ?: this.textColor,
        score = score ?: this.score,
        danmakuStyle = danmakuStyle ?: this.danmakuStyle,
        userId = userId ?: this.userId,
        mergedType = mergedType ?: this.mergedType
    )
