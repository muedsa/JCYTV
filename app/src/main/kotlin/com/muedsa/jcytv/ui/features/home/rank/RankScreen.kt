package com.muedsa.jcytv.ui.features.home.rank

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.items
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.muedsa.compose.tv.theme.ScreenPaddingLeft
import com.muedsa.compose.tv.useLocalErrorMsgBoxController
import com.muedsa.compose.tv.useLocalNavHostController
import com.muedsa.compose.tv.widget.ErrorScreen
import com.muedsa.compose.tv.widget.LoadingScreen
import com.muedsa.jcytv.ui.nav.NavigationItems
import com.muedsa.jcytv.ui.nav.navigate
import com.muedsa.jcytv.viewmodel.RankViewModel
import com.muedsa.model.LazyType
import com.muedsa.uitl.LogUtil
import kotlin.math.min

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun RankScreen(
    viewModel: RankViewModel = hiltViewModel()
) {
    val errorMsgBoxState = useLocalErrorMsgBoxController()
    val navController = useLocalNavHostController()

    val rankListLD by viewModel.rankListLDSF.collectAsState()

    LaunchedEffect(key1 = rankListLD) {
        if (rankListLD.type == LazyType.FAILURE) {
            errorMsgBoxState.error(rankListLD.error)
        }
    }

    Column(modifier = Modifier.padding(start = ScreenPaddingLeft)) {
        if (rankListLD.type == LazyType.SUCCESS && !rankListLD.data.isNullOrEmpty()) {
            val ranks = rankListLD.data!!.subList(0, min(3, rankListLD.data!!.size))
            Row {
                ranks.forEach { rank ->
                    Column(
                        modifier = Modifier
                            .padding(top = 10.dp, bottom = 10.dp, end = 10.dp)
                            .weight(1f)
                    ) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = rank.first,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.titleLarge
                        )
                        TvLazyColumn {
                            items(rank.second) {
                                RankAnimeWidget(
                                    model = it,
                                    onClick = {
                                        LogUtil.d("Click $it")
                                        navController.navigate(
                                            NavigationItems.Detail,
                                            listOf(it.id.toString())
                                        )
                                    }
                                )
                            }
                            item {
                                Spacer(modifier = Modifier.height(100.dp))
                            }
                        }
                    }
                }
            }
        } else if (rankListLD.type == LazyType.LOADING) {
            LoadingScreen()
        } else {
            ErrorScreen {
                viewModel.fetchRankList()
            }
        }
    }
}