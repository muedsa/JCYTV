package com.muedsa.jcytv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.SurfaceDefaults
import com.muedsa.compose.tv.theme.TvTheme
import com.muedsa.compose.tv.useLocalToastMsgBoxController
import com.muedsa.compose.tv.widget.AppBackHandler
import com.muedsa.compose.tv.widget.FillTextScreen
import com.muedsa.compose.tv.widget.Scaffold
import com.muedsa.jcytv.screens.playback.PlaybackScreen
import com.muedsa.uitl.LogUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlaybackActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val aid = intent.getLongExtra(AID_KEY, 0L)
        val episodeTitle = intent.getStringExtra(EPISODE_TITLE_KEY)
        val mediaUrl = intent.getStringExtra(MEDIA_URL_KEY)
        val episodeId = intent.getLongExtra(DAN_EPISODE_ID_KEY, 0)
        LogUtil.fb("exoplayer play url: $mediaUrl")
        setContent {
            TvTheme {
                Scaffold(
                    holdBack = false,
                    enableDrawer = false,
                    colors = SurfaceDefaults.colors(
                        containerColor = Color.Black,
                        contentColor = MaterialTheme.colorScheme.onBackground
                    )
                ) {
                    if (aid <= 0 || episodeTitle.isNullOrEmpty() || mediaUrl.isNullOrEmpty()) {
                        FillTextScreen(context = "视频地址错误")
                    } else {
                        val backListeners = remember {
                            mutableStateListOf<() -> Unit>()
                        }
                        val toastMsgBoxController = useLocalToastMsgBoxController()
                        AppBackHandler {
                            backListeners.forEach {
                                it()
                            }
                            toastMsgBoxController.warning("再次点击返回键退出")
                        }
                        PlaybackScreen(
                            aid = aid,
                            episodeTitle = episodeTitle,
                            mediaUrl = mediaUrl,
                            danEpisodeId = episodeId,
                            backListeners = backListeners
                        )
                    }
                }
            }
        }
    }

    companion object {
        const val AID_KEY = "AID"
        const val EPISODE_TITLE_KEY = "EPISODE_TITLE"
        const val MEDIA_URL_KEY = "MEDIA_URL"
        const val DAN_EPISODE_ID_KEY = "EPISODE_ID"
    }
}