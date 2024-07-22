package com.muedsa.jcytv.ui.features.home.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.OutlinedIconButton
import com.muedsa.compose.tv.model.ContentModel
import com.muedsa.compose.tv.theme.ImageCardRowCardPadding
import com.muedsa.compose.tv.theme.ScreenPaddingLeft
import com.muedsa.compose.tv.theme.outline
import com.muedsa.compose.tv.useLocalErrorMsgBoxController
import com.muedsa.compose.tv.useLocalNavHostController
import com.muedsa.compose.tv.widget.CardType
import com.muedsa.compose.tv.widget.ImageContentCard
import com.muedsa.compose.tv.widget.ScreenBackgroundType
import com.muedsa.jcytv.ui.GirdLastItemHeight
import com.muedsa.jcytv.ui.VideoPosterSize
import com.muedsa.jcytv.ui.features.home.useLocalHomeScreenBackgroundState
import com.muedsa.jcytv.ui.nav.NavigationItems
import com.muedsa.jcytv.ui.nav.navigate
import com.muedsa.jcytv.viewmodel.SearchViewModel
import com.muedsa.model.LazyType
import com.muedsa.uitl.LogUtil


@Composable
fun SearchScreen(
    viewModel: SearchViewModel = hiltViewModel()
) {
    val backgroundState = useLocalHomeScreenBackgroundState()
    val errorMsgBoxState = useLocalErrorMsgBoxController()
    val navController = useLocalNavHostController()

    val searchText by viewModel.searchTextSF.collectAsState()
    val searchAnimeLD by viewModel.searchAnimeLDSF.collectAsState()

    LaunchedEffect(key1 = searchAnimeLD) {
        if (searchAnimeLD.type == LazyType.FAILURE) {
            errorMsgBoxState.error(searchAnimeLD.error)
        }
    }

    Column(modifier = Modifier.padding(start = ScreenPaddingLeft)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .offset(x = -ScreenPaddingLeft)
                .padding(vertical = 30.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth(0.55f)
                    .background(
                        color = MaterialTheme.colorScheme.inverseOnSurface,
                        shape = OutlinedTextFieldDefaults.shape
                    ),
                textStyle = MaterialTheme.typography.bodyLarge,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.outline,
                    cursorColor = MaterialTheme.colorScheme.onSurface,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
                value = searchText,
                onValueChange = {
                    viewModel.searchTextSF.value = it
                },
                singleLine = true
            )
            Spacer(modifier = Modifier.width(16.dp))
            OutlinedIconButton(
                modifier = Modifier.testTag("searchScreen_searchButton"),
                onClick = { viewModel.searchAnime(searchText) }
            ) {
                Icon(
                    modifier = Modifier.size(ButtonDefaults.IconSize),
                    imageVector = Icons.Outlined.Search,
                    contentDescription = "搜索"
                )
            }
        }

        if (!searchAnimeLD.data.isNullOrEmpty()) {
            val animeList = searchAnimeLD.data!!
            val gridFocusRequester = remember { FocusRequester() }

            LazyVerticalGrid(
                columns = GridCells.Adaptive(VideoPosterSize.width + ImageCardRowCardPadding),
                contentPadding = PaddingValues(
                    top = ImageCardRowCardPadding,
                    bottom = ImageCardRowCardPadding
                ),
                modifier = Modifier
                    .focusRequester(gridFocusRequester)
            ) {
                itemsIndexed(
                    items = animeList,
                    key = { _, item -> item.id }
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
    }
}

