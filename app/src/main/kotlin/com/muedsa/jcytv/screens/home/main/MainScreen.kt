package com.muedsa.jcytv.screens.home.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.muedsa.compose.tv.model.ContentModel
import com.muedsa.compose.tv.theme.ImageCardRowCardPadding
import com.muedsa.compose.tv.theme.ScreenPaddingLeft
import com.muedsa.compose.tv.useLocalNavHostController
import com.muedsa.compose.tv.useLocalToastMsgBoxController
import com.muedsa.compose.tv.widget.ContentBlock
import com.muedsa.compose.tv.widget.ErrorScreen
import com.muedsa.compose.tv.widget.ImageCardsRow
import com.muedsa.compose.tv.widget.ImmersiveList
import com.muedsa.compose.tv.widget.LoadingScreen
import com.muedsa.compose.tv.widget.ScreenBackgroundType
import com.muedsa.compose.tv.widget.StandardImageCardsRow
import com.muedsa.jcytv.BuildConfig
import com.muedsa.jcytv.exception.NeedValidateCaptchaException
import com.muedsa.jcytv.model.JcyVideoRow
import com.muedsa.jcytv.screens.NavigationItems
import com.muedsa.jcytv.screens.home.useLocalHomeScreenBackgroundState
import com.muedsa.jcytv.screens.nav
import com.muedsa.jcytv.theme.VideoPosterSize
import com.muedsa.uitl.LogUtil


@Composable
fun MainScreen(
    viewModel: MainScreenViewModel = hiltViewModel(),
) {
    val toastMsgBoxController = useLocalToastMsgBoxController()
    val navController = useLocalNavHostController()

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val s = uiState) {
        is MainScreenUiState.Loading -> LoadingScreen()

        is MainScreenUiState.Error -> ErrorScreen(
            onError = {
                if (s.exception is NeedValidateCaptchaException) {
                    navController.nav(NavigationItems.Captcha)
                } else {
                    toastMsgBoxController.error(s.error)
                }
            },
            onRefresh = { viewModel.refreshData() }
        )

        is MainScreenUiState.Ready -> {
            if (s.rows.isNotEmpty()) {
                MainScreenVideoRows(s.rows[0], s.rows.subList(1, s.rows.size))
            } else {
                ErrorScreen { viewModel.refreshData() }
            }
        }
    }
}

@Composable
fun MainScreenVideoRows(
    first: JcyVideoRow,
    others: List<JcyVideoRow>
) {
    val configuration = LocalConfiguration.current
    val backgroundState = useLocalHomeScreenBackgroundState()
    val navController = useLocalNavHostController()


    val firstRowHeight =
        (MaterialTheme.typography.titleLarge.fontSize.value * configuration.fontScale + 0.5f).dp +
                ImageCardRowCardPadding * 3 + VideoPosterSize.height

    val tabHeight =
        (MaterialTheme.typography.labelLarge.fontSize.value * configuration.fontScale + 0.5f).dp +
                24.dp * 2 +
                6.dp * 2

    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp

    LazyColumn(
        modifier = Modifier
            .padding(start = ScreenPaddingLeft - ImageCardRowCardPadding)
    ) {
        item {
            var title by remember { mutableStateOf("") }
            var subTitle by remember { mutableStateOf<String?>(null) }

            LaunchedEffect(key1 = first) {
                val firstAnime = first.list.firstOrNull()
                if (firstAnime != null) {
                    title = firstAnime.title
                    subTitle = firstAnime.subTitle
                    backgroundState.url = firstAnime.imageUrl
                    backgroundState.type = ScreenBackgroundType.SCRIM
                }
            }
            ImmersiveList(
                background = {
                    ContentBlock(
                        modifier = Modifier
                            .width(screenWidth / 2)
                            .height(screenHeight - firstRowHeight - tabHeight - 20.dp),
                        model = ContentModel(title = title, subtitle = subTitle),
                        descriptionMaxLines = 3
                    )
                },
            ) {
                Column {
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(screenHeight - firstRowHeight - tabHeight)
                    )
                    ImageCardsRow(
                        modifier = Modifier.testTag("mainScreen_row_1"),
                        title = first.title,
                        modelList = first.list,
                        imageFn = { _, anime ->
                            anime.imageUrl
                        },
                        imageSize = VideoPosterSize,
                        onItemFocus = { _, anime ->
                            title = anime.title
                            subTitle = anime.subTitle
                            backgroundState.type = ScreenBackgroundType.SCRIM
                            backgroundState.url = anime.imageUrl
                        },
                        onItemClick = { _, anime ->
                            LogUtil.d("Click $anime")
                            navController.nav(
                                NavigationItems.Detail,
                                listOf(anime.id.toString())
                            )
                        }
                    )
                }
            }
        }

        others.forEachIndexed { index, row ->
            item {
                StandardImageCardsRow(
                    modifier = Modifier.testTag("mainScreen_row_${index + 1}"),
                    title = row.title,
                    modelList = row.list,
                    imageFn = { _, anime ->
                        anime.imageUrl
                    },
                    imageSize = VideoPosterSize,
                    contentFn = { _, anime ->
                        ContentModel(
                            title = anime.title,
                            subtitle = anime.subTitle
                        )
                    },
                    onItemFocus = { _, anime ->
                        backgroundState.type = ScreenBackgroundType.BLUR
                        backgroundState.url = anime.imageUrl
                    },
                    onItemClick = { _, anime ->
                        LogUtil.d("Click $anime")
                        navController.nav(
                            NavigationItems.Detail,
                            listOf(anime.id.toString())
                        )
                    }
                )
            }
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                Text(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .align(Alignment.CenterEnd)
                        .graphicsLayer { alpha = 0.6f },
                    text = "APP版本: ${BuildConfig.BUILD_TYPE}-${BuildConfig.VERSION_NAME}(${BuildConfig.VERSION_CODE})",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}