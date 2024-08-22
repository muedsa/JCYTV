package com.muedsa.jcytv.screens.home.search

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.muedsa.compose.tv.model.ContentModel
import com.muedsa.compose.tv.theme.ImageCardRowCardPadding
import com.muedsa.compose.tv.useLocalNavHostController
import com.muedsa.compose.tv.widget.CardType
import com.muedsa.compose.tv.widget.ImageContentCard
import com.muedsa.compose.tv.widget.ScreenBackgroundType
import com.muedsa.jcytv.model.JcySimpleVideoInfo
import com.muedsa.jcytv.screens.NavigationItems
import com.muedsa.jcytv.screens.home.useLocalHomeScreenBackgroundState
import com.muedsa.jcytv.screens.navigate
import com.muedsa.jcytv.theme.GirdLastItemHeight
import com.muedsa.jcytv.theme.VideoPosterSize
import com.muedsa.uitl.LogUtil

@Composable
fun SearchResult(
    animeList: List<JcySimpleVideoInfo>
) {
    val backgroundState = useLocalHomeScreenBackgroundState()
    val navController = useLocalNavHostController()

    LazyVerticalGrid(
        columns = GridCells.Adaptive(VideoPosterSize.width + ImageCardRowCardPadding),
        contentPadding = PaddingValues(
            top = ImageCardRowCardPadding,
            bottom = ImageCardRowCardPadding
        )
    ) {
        itemsIndexed(
            items = animeList,
            key = { _, item -> item.detailPagePath }
        ) { _, item ->
            ImageContentCard(
                modifier = Modifier.padding(end = ImageCardRowCardPadding),
                url = item.imageUrl,
                imageSize = VideoPosterSize,
                type = CardType.STANDARD,
                model = ContentModel(
                    title = item.title,
                    subtitle = item.subTitle
                ),
                onItemFocus = {
                    backgroundState.url = item.imageUrl
                    backgroundState.type = ScreenBackgroundType.BLUR
                },
                onItemClick = {
                    LogUtil.fb("Click $item")
                    navController.navigate(
                        NavigationItems.Detail,
                        listOf(item.id.toString())
                    )
                }
            )
        }

        // 最后一行占位
        item {
            Spacer(modifier = Modifier.height(GirdLastItemHeight))
        }
    }
}