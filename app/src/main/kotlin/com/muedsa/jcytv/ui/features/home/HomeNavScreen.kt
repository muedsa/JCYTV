package com.muedsa.jcytv.ui.features.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.hilt.navigation.compose.hiltViewModel
import com.muedsa.compose.tv.widget.ScreenBackground
import com.muedsa.compose.tv.widget.ScreenBackgroundState
import com.muedsa.compose.tv.widget.rememberScreenBackgroundState
import com.muedsa.jcytv.viewmodel.HomePageViewModel

private val LocalHomeScreenBackgroundState = compositionLocalOf<ScreenBackgroundState?> { null }

@Composable
fun LocalHomeScreenBackgroundStateProvider(
    backgroundState: ScreenBackgroundState = rememberScreenBackgroundState(),
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(value = LocalHomeScreenBackgroundState provides backgroundState, content = content)
}

@Composable
fun useLocalHomeScreenBackgroundState(): ScreenBackgroundState {
    return LocalHomeScreenBackgroundState.current ?: throw RuntimeException("Please wrap your app with LocalHomeScreenBackgroundState")
}

@Composable
fun HomeNavScreen(
    tabIndex: Int = 0,
    homePageViewModel: HomePageViewModel = hiltViewModel(),
) {
    val backgroundState = rememberScreenBackgroundState()

    ScreenBackground(state = backgroundState)
    LocalHomeScreenBackgroundStateProvider(backgroundState) {
        HomeNavTabWidget(
            tabIndex = tabIndex,
            homePageViewModel = homePageViewModel
        )
    }
}