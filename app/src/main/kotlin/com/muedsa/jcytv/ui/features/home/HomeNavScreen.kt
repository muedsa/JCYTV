package com.muedsa.jcytv.ui.features.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.hilt.navigation.compose.hiltViewModel
import com.muedsa.compose.tv.widget.ScreenBackground
import com.muedsa.compose.tv.widget.ScreenBackgroundState
import com.muedsa.compose.tv.widget.rememberScreenBackgroundState
import com.muedsa.jcytv.viewmodel.HomePageViewModel

val LocalHomeScreenBackgroundState = compositionLocalOf<ScreenBackgroundState> {
    error("LocalHomeScreenBackgroundState not init")
}

@Composable
fun HomeNavScreen(
    tabIndex: Int = 0,
    homePageViewModel: HomePageViewModel = hiltViewModel(),
) {
    val backgroundState = rememberScreenBackgroundState()

    ScreenBackground(state = backgroundState)
    CompositionLocalProvider(value = LocalHomeScreenBackgroundState provides backgroundState) {
        HomeNavTabWidget(
            tabIndex = tabIndex,
            homePageViewModel = homePageViewModel
        )
    }
}