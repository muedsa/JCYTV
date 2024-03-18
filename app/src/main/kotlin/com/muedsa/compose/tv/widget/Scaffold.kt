package com.muedsa.compose.tv.widget

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.NonInteractiveSurfaceColors
import androidx.tv.material3.NonInteractiveSurfaceDefaults
import androidx.tv.material3.Surface

val LocalErrorMsgBoxState = compositionLocalOf<ErrorMessageBoxState> {
    error("localErrorMsgBoxState not init")
}

val LocalRightSideDrawerState = compositionLocalOf<RightSideDrawerState> {
    error("LocalRightSideDrawerState not init")
}

val LocalAppNavCtrl = compositionLocalOf<RightSideDrawerState> {
    error("LocalRightSideDrawerState not init")
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun Scaffold(
    holdBack: Boolean = true,
    colors: NonInteractiveSurfaceColors = NonInteractiveSurfaceDefaults.colors(
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground
    ),
    content: @Composable () -> Unit
) {
    val errorMsgBoxState = remember { ErrorMessageBoxState() }
    val rightSideDrawerState = RightSideDrawerState()
    if (holdBack) {
        AppBackHandler {
            errorMsgBoxState.error("再次点击返回键退出")
        }
    }
    Surface(
        modifier = Modifier.fillMaxSize(),
        shape = RectangleShape,
        colors = colors
    ) {
        ErrorMessageBox(state = errorMsgBoxState) {
            RightSideDrawer(
                state = rightSideDrawerState,
            ) {
                CompositionLocalProvider(
                    LocalErrorMsgBoxState provides errorMsgBoxState,
                    LocalRightSideDrawerState provides rightSideDrawerState
                ) {
                    content()
                }
            }
        }
    }
}