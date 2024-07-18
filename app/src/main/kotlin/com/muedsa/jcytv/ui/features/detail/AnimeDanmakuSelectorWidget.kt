package com.muedsa.jcytv.ui.features.detail

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.items
import androidx.tv.material3.Button
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.OutlinedIconButton
import androidx.tv.material3.RadioButton
import androidx.tv.material3.Text
import androidx.tv.material3.WideButtonDefaults
import com.muedsa.compose.tv.theme.outline
import com.muedsa.compose.tv.useLocalRightSideDrawerController
import com.muedsa.compose.tv.widget.NoBackground
import com.muedsa.compose.tv.widget.TwoSideWideButton
import com.muedsa.jcytv.viewmodel.AnimeDetailViewModel

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun AnimeDanmakuSelectBtnWidget(
    enabledDanmakuState: MutableState<Boolean> = remember { mutableStateOf(false) },
    viewModel: AnimeDetailViewModel = hiltViewModel(),
) {
    val drawerController = useLocalRightSideDrawerController()

    OutlinedIconButton(onClick = {
        drawerController.pop {
            DanmakuSelectorSideWidget(enabledDanmakuState, viewModel)
        }
    }) {
        Icon(
            imageVector = Icons.Outlined.Edit,
            contentDescription = "修改弹弹Play匹配剧集"
        )
    }
}

@OptIn(ExperimentalTvMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun DanmakuSelectorSideWidget(
    enabledDanmakuState: MutableState<Boolean> = remember { mutableStateOf(false) },
    viewModel: AnimeDetailViewModel = hiltViewModel(),
) {
    val drawerController = useLocalRightSideDrawerController()

    var isSelectorTab by remember { mutableStateOf(true) }

    val danSearchAnimeListLD by viewModel.danSearchAnimeListLDSF.collectAsState()
    val danSearchAnimeList = danSearchAnimeListLD.data ?: emptyList()
    val danAnimeInfoLD by viewModel.danAnimeInfoLDSF.collectAsState()
    val danSearchTitleState = viewModel.danSearchTitleSF.collectAsState()
    var searchTitle by remember { mutableStateOf(danSearchTitleState.value ?: "") }

    Column {
        if (isSelectorTab) {
            Text(
                modifier = Modifier
                    .padding(start = 8.dp, end = 15.dp),
                text = "弹幕剧集",
                style = MaterialTheme.typography.titleLarge
            )
            TvLazyColumn(
                contentPadding = PaddingValues(vertical = 20.dp)
            ) {
                item {
                    val interactionSource = remember { MutableInteractionSource() }
                    TwoSideWideButton(
                        title = { Text("关闭弹幕") },
                        onClick = {
                            drawerController.close()
                            enabledDanmakuState.value = false
                        },
                        interactionSource = interactionSource,
                        background = {
                            WideButtonDefaults.NoBackground(
                                interactionSource = interactionSource
                            )
                        }
                    ) {
                        RadioButton(
                            selected = !enabledDanmakuState.value,
                            onClick = { },
                            interactionSource = interactionSource
                        )
                    }
                }
                items(items = danSearchAnimeList) {
                    val interactionSource = remember { MutableInteractionSource() }
                    TwoSideWideButton(
                        title = {
                            Text(
                                modifier = Modifier.basicMarquee(),
                                text = "${it.animeTitle} - ${it.typeDescription} - ${it.startOnlyDate}"
                            )
                        },
                        onClick = {
                            drawerController.close()
                            enabledDanmakuState.value = true
                            viewModel.danBangumi(it.animeId)
                        },
                        interactionSource = interactionSource,
                        background = {
                            WideButtonDefaults.NoBackground(
                                interactionSource = interactionSource
                            )
                        }
                    ) {
                        RadioButton(
                            selected = enabledDanmakuState.value
                                    && danAnimeInfoLD.data?.animeId == it.animeId,
                            onClick = { },
                            interactionSource = interactionSource
                        )
                    }
                }

                item {
                    val interactionSource = remember { MutableInteractionSource() }
                    TwoSideWideButton(
                        title = { Text(text = "未找到？") },
                        onClick = {
                            isSelectorTab = false
                        },
                        interactionSource = interactionSource,
                        background = {
                            WideButtonDefaults.NoBackground(
                                interactionSource = interactionSource
                            )
                        }
                    ) {
                        Text(text = "手动搜索")
                    }
                }
            }
        } else {
            Text(
                modifier = Modifier
                    .padding(start = 8.dp, end = 15.dp),
                text = "手动搜索",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(20.dp))
            OutlinedTextField(
                modifier = Modifier
                    .width(256.dp)
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
                value = searchTitle,
                onValueChange = {
                    searchTitle = it
                },
                singleLine = true
            )
            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.width(256.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        viewModel.danSearchTitleSF.value = searchTitle
                        isSelectorTab = true
                    }
                ) {
                    Text("搜索")
                }

                Button(
                    onClick = {
                        isSelectorTab = true
                    }
                ) {
                    Text("返回")
                }
            }
        }
    }
}