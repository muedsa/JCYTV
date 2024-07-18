package com.muedsa.jcytv.ui.features.detail

import android.content.Intent
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.LocalContentColor
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.OutlinedButton
import androidx.tv.material3.OutlinedIconButton
import androidx.tv.material3.RadioButton
import androidx.tv.material3.Text
import androidx.tv.material3.WideButtonDefaults
import com.muedsa.compose.tv.model.ContentModel
import com.muedsa.compose.tv.theme.ScreenPaddingLeft
import com.muedsa.compose.tv.useLocalErrorMsgBoxController
import com.muedsa.compose.tv.useLocalNavHostController
import com.muedsa.compose.tv.useLocalRightSideDrawerController
import com.muedsa.compose.tv.widget.ContentBlock
import com.muedsa.compose.tv.widget.ContentBlockType
import com.muedsa.compose.tv.widget.EmptyDataScreen
import com.muedsa.compose.tv.widget.ErrorScreen
import com.muedsa.compose.tv.widget.LoadingScreen
import com.muedsa.compose.tv.widget.NoBackground
import com.muedsa.compose.tv.widget.ScreenBackground
import com.muedsa.compose.tv.widget.ScreenBackgroundType
import com.muedsa.compose.tv.widget.TwoSideWideButton
import com.muedsa.compose.tv.widget.rememberScreenBackgroundState
import com.muedsa.jcytv.PlaybackActivity
import com.muedsa.jcytv.room.model.FavoriteAnimeModel
import com.muedsa.jcytv.ui.FavoriteIconColor
import com.muedsa.jcytv.ui.RankFontColor
import com.muedsa.jcytv.ui.RankIconColor
import com.muedsa.jcytv.ui.nav.NavigationItems
import com.muedsa.jcytv.ui.nav.navigate
import com.muedsa.jcytv.util.Upscayl
import com.muedsa.jcytv.viewmodel.AnimeDetailViewModel
import com.muedsa.jcytv.viewmodel.AppSettingViewModel
import com.muedsa.model.LazyType
import com.muedsa.uitl.LogUtil

@Composable
fun AnimeDetailScreen(
    viewModel: AnimeDetailViewModel = hiltViewModel(),
    appSettingViewModel: AppSettingViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp
    val lifecycleOwner = LocalLifecycleOwner.current
    val errorMsgBoxController = useLocalErrorMsgBoxController()
    val drawerController = useLocalRightSideDrawerController()
    val navController = useLocalNavHostController()

    val animeDetailLD by viewModel.animeDetailLDSF.collectAsState()
    val favoriteModel by viewModel.favoriteModelSF.collectAsState()
    val watchedEpisodeTitleMap by viewModel.watchedEpisodeTitleMapSF.collectAsState()
    val danSearchAnimeListLD by viewModel.danSearchAnimeListLDSF.collectAsState()
    val danAnimeInfoLD by viewModel.danAnimeInfoLDSF.collectAsState()

    val settingLD by appSettingViewModel.settingLDSF.collectAsState()

    val episodeRelationMap = remember { mutableStateMapOf<String, Long>() }

    val backgroundState = rememberScreenBackgroundState(
        initType = ScreenBackgroundType.SCRIM
    )

    LaunchedEffect(key1 = animeDetailLD) {
        if (animeDetailLD.type == LazyType.FAILURE) {
            errorMsgBoxController.error(animeDetailLD.error)
        } else if (animeDetailLD.type == LazyType.SUCCESS) {
            if (animeDetailLD.data != null) {
                if (settingLD.data?.upscaylCoverImageEnable == true) {
                    backgroundState.url = Upscayl.url(animeDetailLD.data!!.imageUrl)
                } else {
                    backgroundState.url = animeDetailLD.data!!.imageUrl
                }
            }
        }
    }

    LaunchedEffect(key1 = danSearchAnimeListLD) {
        if (danSearchAnimeListLD.type == LazyType.FAILURE) {
            errorMsgBoxController.error(danSearchAnimeListLD.error)
        }
    }

    LaunchedEffect(key1 = danAnimeInfoLD) {
        if (danAnimeInfoLD.type == LazyType.FAILURE) {
            errorMsgBoxController.error(danAnimeInfoLD.error)
        }
    }

    DisposableEffect(key1 = lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refreshWatchedEpisodeTitleSet()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    ScreenBackground(backgroundState)

    if (animeDetailLD.type == LazyType.SUCCESS) {
        if (animeDetailLD.data != null) {
            val animeDetail = animeDetailLD.data!!

            // 播放源 to 剧集列表
            var selectedPlaySource by remember {
                mutableStateOf<Pair<String, List<Pair<String, String>>>?>(null)
            }
            LaunchedEffect(key1 = animeDetail) {
                selectedPlaySource = animeDetail.playList.firstOrNull()
            }

            val enabledDanmakuState = remember { mutableStateOf(true) }

            LazyColumn(
                modifier = Modifier
                    .padding(start = ScreenPaddingLeft),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                // top space
                item {
                    // 占位锚点 使之可以通过Dpad返回页面的顶部
                    Spacer(modifier = Modifier.focusable())
                }

                // 介绍
                item {
                    ContentBlock(
                        modifier = Modifier
                            .padding(top = screenHeight * 0.2f)
                            .width(screenWidth * 0.60f),
                        model = ContentModel(
                            title = animeDetail.title,
                            subtitle = animeDetail.status,
                            description = animeDetail.description,
                        ),
                        type = ContentBlockType.CAROUSEL,
                        verticalArrangement = Arrangement.Top,
                        descriptionMaxLines = 10
                    )
                    Spacer(modifier = Modifier.height(25.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (danAnimeInfoLD.type == LazyType.SUCCESS && danAnimeInfoLD.data != null) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = "评分",
                                tint = RankIconColor
                            )
                            Text(
                                modifier = Modifier
                                    .width(70.dp)
                                    .padding(start = 8.dp, end = 15.dp),
                                text = "${danAnimeInfoLD.data!!.rating}",
                                color = RankFontColor,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(25.dp))
                }

                // 按钮列表
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // 切换播放源
                        Text(
                            text = "播放源: ${selectedPlaySource?.first ?: "--"}",
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedIconButton(onClick = {
                            drawerController.pop {
                                Column {
                                    Text(
                                        modifier = Modifier
                                            .padding(start = 8.dp, end = 15.dp),
                                        text = "弹幕剧集",
                                        style = MaterialTheme.typography.titleLarge
                                    )
                                    LazyColumn(
                                        contentPadding = PaddingValues(vertical = 20.dp)
                                    ) {
                                        items(items = animeDetail.playList) {
                                            val interactionSource =
                                                remember { MutableInteractionSource() }
                                            TwoSideWideButton(
                                                title = { Text(text = it.first) },
                                                onClick = {
                                                    selectedPlaySource = it
                                                    drawerController.close()
                                                },
                                                interactionSource = interactionSource,
                                                background = {
                                                    WideButtonDefaults.NoBackground(
                                                        interactionSource = interactionSource
                                                    )
                                                }
                                            ) {
                                                RadioButton(
                                                    selected = selectedPlaySource == it,
                                                    onClick = { },
                                                    interactionSource = interactionSource
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Outlined.Edit,
                                contentDescription = "修改播放源"
                            )
                        }

                        // 收藏按钮
                        Spacer(modifier = Modifier.width(25.dp))
                        OutlinedButton(onClick = {
                            if (favoriteModel == null) {
                                viewModel.favorite(
                                    model = FavoriteAnimeModel(
                                        id = animeDetail.id,
                                        name = animeDetail.title,
                                        cover = animeDetail.imageUrl,
                                        updateAt = System.currentTimeMillis()
                                    ),
                                    favorite = true
                                )
                            } else {
                                viewModel.favorite(model = favoriteModel!!, favorite = false)
                            }
                        }) {
                            Text(text = if (favoriteModel == null) "追番" else "已追")
                            Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
                            Icon(
                                imageVector = Icons.Outlined.Favorite,
                                contentDescription = "收藏",
                                tint = if (favoriteModel == null) LocalContentColor.current else FavoriteIconColor
                            )
                        }

                        // 切换弹弹Play匹配剧集
                        val danAnimeInfo = danAnimeInfoLD.data
                        Spacer(modifier = Modifier.width(25.dp))
                        Text(
                            text = "弹弹Play匹配剧集: ",
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            modifier = Modifier
                                .widthIn(max = 128.dp)
                                .basicMarquee(),
                            text = if (enabledDanmakuState.value) danAnimeInfo?.animeTitle
                                ?: "--" else "关闭",
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.titleMedium
                        )
                        AnimeDanmakuSelectBtnWidget(
                            enabledDanmakuState = enabledDanmakuState,
                            viewModel = viewModel
                        )

                        // 设置按钮
                        Spacer(modifier = Modifier.width(25.dp))
                        OutlinedButton(
                            onClick = {
                                navController.navigate(NavigationItems.Setting)
                            }
                        ) {
                            Text(text = "设置")
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }

                if (!selectedPlaySource?.second.isNullOrEmpty()) {
                    val episodeList = selectedPlaySource!!.second

                    // 剧集列表
                    item {
                        EpisodeListWidget(
                            episodeList = episodeList,
                            danEpisodeList = danAnimeInfoLD.data?.episodes ?: emptyList(),
                            episodeProgressMap = watchedEpisodeTitleMap,
                            episodeRelationMap = episodeRelationMap,
                            onEpisodeClick = { episode, danEpisode ->
                                LogUtil.fb("click play: $episode")
                                viewModel.parseRealVideoUrlForDetailPlaySourceUrl(
                                    url = episode.second,
                                    onSuccess = {
                                        val intent = Intent(context, PlaybackActivity::class.java)
                                        intent.putExtra(
                                            PlaybackActivity.AID_KEY,
                                            animeDetail.id
                                        )
                                        intent.putExtra(
                                            PlaybackActivity.EPISODE_TITLE_KEY,
                                            episode.first
                                        )
                                        intent.putExtra(PlaybackActivity.MEDIA_URL_KEY, it)
                                        if (enabledDanmakuState.value && danEpisode != null) {
                                            intent.putExtra(
                                                PlaybackActivity.DAN_EPISODE_ID_KEY,
                                                danEpisode.episodeId
                                            )
                                        }
                                        context.startActivity(intent)
                                    },
                                    onError = {
                                        errorMsgBoxController.error(it)
                                    }
                                )
                            },
                            onChangeEpisodeRelation = {
                                it.forEach { pair ->
                                    episodeRelationMap[pair.first] = pair.second.episodeId
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(25.dp))
                    }
                }


                // TODO 猜你喜欢

                // TODO 新番放送排行榜

                // bottom space
                item {
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        } else {
            EmptyDataScreen()
        }
    } else if (animeDetailLD.type == LazyType.FAILURE) {
        ErrorScreen {
            viewModel.animeIdSF.value = viewModel.animeIdSF.value
        }
    } else {
        LoadingScreen()
    }

}