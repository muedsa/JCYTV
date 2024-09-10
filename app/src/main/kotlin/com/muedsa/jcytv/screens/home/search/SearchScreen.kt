package com.muedsa.jcytv.screens.home.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.muedsa.compose.tv.theme.ScreenPaddingLeft
import com.muedsa.compose.tv.useLocalNavHostController
import com.muedsa.compose.tv.useLocalToastMsgBoxController
import com.muedsa.compose.tv.widget.ErrorScreen
import com.muedsa.compose.tv.widget.LoadingScreen
import com.muedsa.jcytv.exception.NeedValidateCaptchaException
import com.muedsa.jcytv.screens.NavigationItems
import com.muedsa.jcytv.screens.nav


@Composable
fun SearchScreen(
    viewModel: SearchViewModel = hiltViewModel()
) {

    val toastMsgBoxController = useLocalToastMsgBoxController()
    val navController = useLocalNavHostController()

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = Modifier.padding(start = ScreenPaddingLeft)) {
        SearchInput(searching = uiState is SearchScreenUiState.Searching) { viewModel.searchAnime(it) }

        when (val s = uiState) {
            is SearchScreenUiState.Searching -> LoadingScreen()

            is SearchScreenUiState.Error -> ErrorScreen(
                onError = {
                    if (s.exception is NeedValidateCaptchaException) {
                        navController.nav(NavigationItems.Captcha)
                    } else {
                        toastMsgBoxController.error(s.error)
                    }
                }
            )

            is SearchScreenUiState.Done -> SearchResult(s.animeList)
        }
    }
}

