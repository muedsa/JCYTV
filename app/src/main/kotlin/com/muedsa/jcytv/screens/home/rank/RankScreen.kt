package com.muedsa.jcytv.screens.home.rank

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.muedsa.compose.tv.useLocalErrorMsgBoxController
import com.muedsa.compose.tv.useLocalNavHostController
import com.muedsa.compose.tv.widget.ErrorScreen
import com.muedsa.compose.tv.widget.LoadingScreen
import com.muedsa.jcytv.exception.NeedValidateCaptchaException
import com.muedsa.jcytv.screens.NavigationItems
import com.muedsa.jcytv.screens.navigate


@Composable
fun RankScreen(
    viewModel: RankViewModel = hiltViewModel()
) {
    val errorMsgBoxController = useLocalErrorMsgBoxController()
    val navController = useLocalNavHostController()

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val s = uiState) {
        is RankScreenUiState.Loading -> LoadingScreen()

        is RankScreenUiState.Error -> ErrorScreen(
            onError = {
                if (s.exception is NeedValidateCaptchaException) {
                    navController.navigate(NavigationItems.Captcha)
                } else {
                    errorMsgBoxController.error(s.error)
                }
            },
            onRefresh = {
                viewModel.fetchRankList()
            }
        )

        is RankScreenUiState.Ready -> RankWidget(list = s.rankList)
    }
}