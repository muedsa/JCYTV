package com.muedsa.compose.tv.widget

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.NonInteractiveSurfaceColors
import androidx.tv.material3.NonInteractiveSurfaceDefaults
import androidx.tv.material3.Surface
import com.muedsa.compose.tv.LocalErrorMsgBoxControllerProvider
import com.muedsa.compose.tv.LocalRightSideDrawerControllerProvider


@Composable
fun Scaffold(
    holdBack: Boolean = true,
    colors: NonInteractiveSurfaceColors = NonInteractiveSurfaceDefaults.colors(
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground
    ),
    content: @Composable () -> Unit
) {
    val errorMessageBoxController = remember { ErrorMessageBoxController() }
    val drawerController = remember { RightSideDrawerController() }

    LocalErrorMsgBoxControllerProvider(errorMessageBoxController) {
        LocalRightSideDrawerControllerProvider(drawerController) {
            if (holdBack) {
                AppBackHandler {
                    errorMessageBoxController.error("再次点击返回键退出")
                }
            }
            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = RectangleShape,
                colors = colors
            ) {
                ErrorMessageBox(state = errorMessageBoxController) {
                    RightSideDrawer(
                        controller = drawerController,
                    ) {
                        content()
                    }
                }
            }
        }
    }
}