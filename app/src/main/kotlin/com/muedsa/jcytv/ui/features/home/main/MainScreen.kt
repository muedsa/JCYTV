package com.muedsa.jcytv.ui.features.home.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.material3.MaterialTheme
import com.muedsa.compose.tv.model.ContentModel
import com.muedsa.compose.tv.theme.ImageCardRowCardPadding
import com.muedsa.compose.tv.theme.ScreenPaddingLeft
import com.muedsa.compose.tv.useLocalErrorMsgBoxController
import com.muedsa.compose.tv.useLocalNavHostController
import com.muedsa.compose.tv.widget.ContentBlock
import com.muedsa.compose.tv.widget.ErrorScreen
import com.muedsa.compose.tv.widget.ImageCardsRow
import com.muedsa.compose.tv.widget.ImmersiveList
import com.muedsa.compose.tv.widget.LoadingScreen
import com.muedsa.compose.tv.widget.ScreenBackgroundType
import com.muedsa.compose.tv.widget.StandardImageCardsRow
import com.muedsa.jcytv.ui.VideoPosterSize
import com.muedsa.jcytv.ui.features.home.useLocalHomeScreenBackgroundState
import com.muedsa.jcytv.ui.nav.NavigationItems
import com.muedsa.jcytv.ui.nav.navigate
import com.muedsa.jcytv.viewmodel.HomePageViewModel
import com.muedsa.model.LazyType
import com.muedsa.uitl.LogUtil


@Composable
fun MainScreen(
    viewModel: HomePageViewModel = hiltViewModel(),
) {
    val configuration = LocalConfiguration.current

    val backgroundState = useLocalHomeScreenBackgroundState()
    val errorMsgBoxState = useLocalErrorMsgBoxController()
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

    val homeRowsLD by viewModel.homeRowsSF.collectAsState()

    LaunchedEffect(key1 = homeRowsLD.type, key2 = homeRowsLD.error) {
        if (homeRowsLD.type == LazyType.FAILURE) {
            errorMsgBoxState.error(homeRowsLD.error)
        }
    }

    if (homeRowsLD.type == LazyType.SUCCESS && !homeRowsLD.data.isNullOrEmpty()) {
        val homeRows = homeRowsLD.data!!
        LazyColumn(
            modifier = Modifier
                .offset(x = ScreenPaddingLeft - ImageCardRowCardPadding),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            item {
                var title by remember { mutableStateOf("") }
                var subTitle by remember { mutableStateOf<String?>(null) }
                val firstRow = homeRows[0]

                LaunchedEffect(key1 = firstRow) {
                    val firstAnime = firstRow.second.firstOrNull()
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
                            title = firstRow.first,
                            modelList = firstRow.second,
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
                                navController.navigate(
                                    NavigationItems.Detail,
                                    listOf(anime.id.toString())
                                )
                            }
                        )
                    }
                }
            }

            homeRows.subList(1, homeRows.size).forEach { row ->
                item {
                    StandardImageCardsRow(
                        title = row.first,
                        modelList = row.second,
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
                            navController.navigate(
                                NavigationItems.Detail,
                                listOf(anime.id.toString())
                            )
                        }
                    )
                }
            }
        }
    } else if (homeRowsLD.type == LazyType.LOADING) {
        LoadingScreen()
    } else {
        ErrorScreen {
            viewModel.refreshHomeData()
        }
    }
}