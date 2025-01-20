package com.muedsa.jcytv.screens.home.rank

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.muedsa.compose.tv.useLocalToastMsgBoxController
import com.muedsa.compose.tv.widget.ErrorScreen
import com.muedsa.compose.tv.widget.LoadingScreen


@Composable
fun RankScreen(
    viewModel: RankViewModel = hiltViewModel()
) {
    val toastMsgBoxController = useLocalToastMsgBoxController()

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val s = uiState) {
        is RankScreenUiState.Loading -> LoadingScreen()

        is RankScreenUiState.Error -> ErrorScreen(
            onError = { toastMsgBoxController.error(s.error) },
            onRefresh = { viewModel.fetchRankList() }
        )

        is RankScreenUiState.Ready -> RankWidget(list = s.rankList)
    }
}