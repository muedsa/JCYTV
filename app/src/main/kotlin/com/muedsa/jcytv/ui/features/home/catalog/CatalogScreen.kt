package com.muedsa.jcytv.ui.features.home.catalog

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.foundation.lazy.grid.TvGridCells
import androidx.tv.foundation.lazy.grid.TvLazyVerticalGrid
import androidx.tv.foundation.lazy.grid.itemsIndexed
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.Card
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.OutlinedButton
import androidx.tv.material3.OutlinedIconButton
import androidx.tv.material3.Text
import com.muedsa.compose.tv.model.ContentModel
import com.muedsa.compose.tv.theme.ImageCardRowCardPadding
import com.muedsa.compose.tv.theme.ScreenPaddingLeft
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
import com.muedsa.jcytv.viewmodel.CatalogViewModel
import com.muedsa.model.LazyType
import com.muedsa.uitl.LogUtil

@OptIn(ExperimentalTvMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun CatalogScreen(
    viewModel: CatalogViewModel = hiltViewModel()
) {
    val navController = useLocalNavHostController()
    val backgroundState = useLocalHomeScreenBackgroundState()
    val errorMsgBoxController = useLocalErrorMsgBoxController()

    var optionId by viewModel.optionIdState
    var optionArea by viewModel.optionAreaState
    var optionClass by viewModel.optionClassState
    var optionLang by viewModel.optionLangState
    var optionYear by viewModel.optionYearState
    var optionLetter by viewModel.optionLetterState
    var optionBy by viewModel.optionByState

    val searchAnimeLP by viewModel.animeLPSF.collectAsState()

    var optionsExpand by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(key1 = searchAnimeLP) {
        if (searchAnimeLP.type == LazyType.FAILURE) {
            errorMsgBoxController.error(searchAnimeLP.error)
        }
    }

    BackHandler(enabled = optionsExpand) {
        optionsExpand = false
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
            OutlinedButton(onClick = {
                optionsExpand = !optionsExpand
            }) {
                Text(text = "筛选项")
                Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
                Icon(
                    modifier = Modifier.size(ButtonDefaults.IconSize),
                    imageVector = if (optionsExpand) Icons.Outlined.KeyboardArrowUp else Icons.Outlined.ArrowDropDown,
                    contentDescription = "展开筛选项"
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            OutlinedIconButton(onClick = {
                viewModel.resetCatalogOptions()
            }) {
                Icon(
                    modifier = Modifier.size(ButtonDefaults.IconSize),
                    imageVector = Icons.Outlined.Refresh,
                    contentDescription = "重置筛选项"
                )
            }
        }

        if (optionsExpand) {
            // 筛选项
            TvLazyColumn(contentPadding = PaddingValues(top = ImageCardRowCardPadding)) {
                item {
                    CatalogOptionsWidget(
                        title = "频道",
                        selectedKey = optionId,
                        options = CatalogViewModel.ID_OPTIONS,
                        onClick = { key, _ ->
                            optionId = key
                            viewModel.catalogNew()
                        }
                    )
                }
                item {
                    CatalogOptionsWidget(
                        title = "地区",
                        selectedKey = optionArea,
                        options = CatalogViewModel.AREA_OPTIONS,
                        onClick = { key, _ ->
                            optionArea = if (optionArea == key) null else key
                            viewModel.catalogNew()
                        }
                    )
                }
                item {
                    CatalogOptionsWidget(
                        title = "剧情",
                        selectedKey = optionClass,
                        options = CatalogViewModel.CLASS_OPTIONS,
                        onClick = { key, _ ->
                            optionClass = if (optionClass == key) null else key
                            viewModel.catalogNew()
                        }
                    )
                }
                item {
                    CatalogOptionsWidget(
                        title = "语言",
                        selectedKey = optionLang,
                        options = CatalogViewModel.LANG_OPTIONS,
                        onClick = { key, _ ->
                            optionLang = if (optionLang == key) null else key
                            viewModel.catalogNew()
                        }
                    )
                }
                item {
                    CatalogOptionsWidget(
                        title = "年份",
                        selectedKey = optionYear,
                        options = CatalogViewModel.YEAR_OPTIONS,
                        onClick = { key, _ ->
                            optionYear = if (optionYear == key) null else key
                            viewModel.catalogNew()
                        }
                    )
                }
                item {
                    CatalogOptionsWidget(
                        title = "字母",
                        selectedKey = optionLetter,
                        options = CatalogViewModel.LETTER_OPTIONS,
                        onClick = { key, _ ->
                            optionLetter = if (optionLetter == key) null else key
                            viewModel.catalogNew()
                        }
                    )
                }
                item {
                    CatalogOptionsWidget(
                        title = "排序",
                        selectedKey = optionBy,
                        options =CatalogViewModel.ORDER_BY_OPTIONS,
                        onClick = { key, _ ->
                            optionBy = if (optionBy == key) null else key
                            viewModel.catalogNew()
                        }
                    )
                }
            }
        } else {
            val gridFocusRequester = remember { FocusRequester() }

            TvLazyVerticalGrid(
                columns = TvGridCells.Adaptive(VideoPosterSize.width + ImageCardRowCardPadding),
                contentPadding = PaddingValues(
                    top = ImageCardRowCardPadding,
                    bottom = ImageCardRowCardPadding
                ),
                modifier = Modifier
                    .focusRequester(gridFocusRequester)
            ) {
                itemsIndexed(
                    items = searchAnimeLP.list,
                    key = { _, item -> item.id }
                ) { index, item ->
                    val itemFocusRequester = remember {
                        FocusRequester()
                    }
                    ImageContentCard(
                        modifier = Modifier
                            .padding(end = ImageCardRowCardPadding)
                            .focusRequester(itemFocusRequester),
                        url = item.imageUrl,
                        imageSize = VideoPosterSize,
                        type = CardType.STANDARD,
                        model = ContentModel(
                            item.title,
                            subtitle = item.subTitle,
                        ),
                        onItemFocus = {
                            backgroundState.url = item.imageUrl
                            backgroundState.type = ScreenBackgroundType.BLUR
                        },
                        onItemClick = {
                            LogUtil.d("Click $item")
                            navController.navigate(
                                NavigationItems.Detail,
                                listOf(item.id.toString())
                            )
                        }
                    )

                    LaunchedEffect(key1 = Unit) {
                        if (searchAnimeLP.offset == index) {
                            itemFocusRequester.requestFocus()
                        }
                    }
                }

                if (searchAnimeLP.type != LazyType.LOADING && searchAnimeLP.hasNext) {
                    item {
                        Card(
                            modifier = Modifier
                                .size(VideoPosterSize)
                                .padding(end = ImageCardRowCardPadding),
                            onClick = {
                                viewModel.catalog(searchAnimeLP)
                            }
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = "继续加载")
                            }
                        }
                    }
                }

                // 最后一行占位
                item {
                    Spacer(modifier = Modifier.height(GirdLastItemHeight))
                }
            }
        }
    }
}

