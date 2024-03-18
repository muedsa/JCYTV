package com.muedsa.compose.tv.widget

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun ErrorMessageBox(
    state: ErrorMessageBoxState = remember { ErrorMessageBoxState() },
    content: @Composable () -> Unit = {}
) {
    LaunchedEffect(key1 = state.duration) {
        delay(1.seconds)
        if (state.duration > 0) {
            state.duration--
        } else {
            state.visible = false
        }
    }
    content()
    AnimatedVisibility(
        visible = state.visible,
        enter = slideInHorizontally { -it },
        exit = slideOutHorizontally { -it / 2 } + fadeOut()
    ) {
        Row(
            modifier = Modifier
                .offset(y = 100.dp)
                .height(IntrinsicSize.Min)
        ) {
            Text(
                modifier = Modifier
                    .weight(8f)
                    .shadow(
                        elevation = 10.dp,
                        shape = RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp)
                    )
                    .background(color = MaterialTheme.colorScheme.errorContainer)
                    .padding(20.dp)
                    .wrapContentWidth(Alignment.Start),
                text = "${state.message}",
                color = MaterialTheme.colorScheme.onErrorContainer,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.weight(2f))
        }

    }
}


@Stable
class ErrorMessageBoxState(
    initVisible: Boolean = false,
    initMessage: String? = null,
    initDuration: Int = 0
) {
    var visible by mutableStateOf(initVisible)
    var message by mutableStateOf(initMessage)
    var duration by mutableIntStateOf(initDuration)

    //    private val mutex = Mutex()
//
//    suspend fun error(message: String, duration: SnackbarDuration) = mutex.withLock {
//        this.visible = true
//        this.message = message
//        this.duration = when(duration) {
//            SnackbarDuration.Short -> 3
//            SnackbarDuration.Long -> 5
//            SnackbarDuration.Indefinite -> -1
//            else -> -1
//        }
//    }
//
//    suspend fun clear() = mutex.withLock {
//        this.visible = false
//        this.message = null
//        this.duration = 0
//    }
    fun error(message: String = "error", duration: SnackbarDuration = SnackbarDuration.Short) {
        this.visible = true
        this.message = message
        this.duration = when (duration) {
            SnackbarDuration.Short -> 3
            SnackbarDuration.Long -> 5
            SnackbarDuration.Indefinite -> -1
            else -> -1
        }
    }

    fun error(error: Throwable?, duration: SnackbarDuration = SnackbarDuration.Short) {
        this.visible = true
        this.message = error?.localizedMessage ?: "error"
        this.duration = when (duration) {
            SnackbarDuration.Short -> 3
            SnackbarDuration.Long -> 5
            SnackbarDuration.Indefinite -> -1
            else -> -1
        }
    }

    fun clear() {
        this.visible = false
        this.message = null
        this.duration = 0
    }
}