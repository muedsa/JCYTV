package com.muedsa.jcytv.screens.captcha

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil.ImageLoader
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.muedsa.compose.tv.useLocalNavHostController
import com.muedsa.compose.tv.useLocalToastMsgBoxController
import com.muedsa.compose.tv.widget.onDpadKeyEvents
import com.muedsa.jcytv.screens.NavigationItems
import com.muedsa.jcytv.screens.navigate
import com.muedsa.jcytv.util.JcyRotateCaptchaTool
import com.muedsa.uitl.LogUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import java.io.IOException


const val ONCE_ROTATE_DEGREES = 3f

@Composable
fun CaptchaScreen(
    captchaViewModel: CaptchaViewModel = hiltViewModel()
) {

    val navHostController = useLocalNavHostController()
    val toastMsgBoxController = useLocalToastMsgBoxController()

    val uiState by captchaViewModel.uiState.collectAsStateWithLifecycle()

    var imageUrl by remember { mutableStateOf(JcyRotateCaptchaTool.buildCaptchaImageUrl()) }
    var degrees by remember { mutableFloatStateOf(0f) }

    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val imageSize = remember { (configuration.screenHeightDp / 3).dp }

    val imageLoader = remember {
        ImageLoader.Builder(context)
            .okHttpClient {
                OkHttpClient.Builder()
                    .addInterceptor(GetGuardFromRespHeaderInterceptor(captchaViewModel))
                    .build()
            }
            .build()
    }

    LaunchedEffect(uiState) {
        if (uiState is CaptchaState.Success) {
            navigateAfterValid(navHostController)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("请先完成图片旋转验证", style = MaterialTheme.typography.displayMedium)
        Spacer(Modifier.height(20.dp))
        Box(modifier = Modifier
            .size(imageSize)
            .clip(CircleShape)
            .focusable(true)
            .onDpadKeyEvents(
                onLeft = {
                    val d = degrees - ONCE_ROTATE_DEGREES
                    degrees = if (d < 0f) 0f else d
                    return@onDpadKeyEvents true
                },
                onRight = {
                    val d = degrees + ONCE_ROTATE_DEGREES
                    degrees = if (d > 360f) 360f else d
                    return@onDpadKeyEvents true
                },
                onCenter = {
                    LogUtil.d("captcha image degrees = $degrees")
                    if (uiState is CaptchaState.Ready) {
                        captchaViewModel.validate((uiState as CaptchaState.Ready).guard, degrees)
                    } else if (uiState is CaptchaState.Retry) {
                        imageUrl = JcyRotateCaptchaTool.buildCaptchaImageUrl()
                        degrees = 0f
                    }
                    return@onDpadKeyEvents true
                }
            )
            .drawWithCache {
                val widthHalf = size.width / 2
                val heightHalf = size.height / 2
                val strokeWidth = 2.dp.toPx()
                val pathEffect = PathEffect.dashPathEffect(floatArrayOf(3f, 3f), 0f)
                onDrawWithContent {
                    drawContent()
                    drawLine(
                        Color.Red,
                        start = Offset(widthHalf, 0f),
                        end = Offset(widthHalf, size.height),
                        strokeWidth = strokeWidth,
                        pathEffect = pathEffect
                    )
                    drawLine(
                        Color.Red,
                        Offset(0f, heightHalf),
                        Offset(size.width, heightHalf),
                        strokeWidth = strokeWidth,
                        pathEffect = pathEffect
                    )
                }
            }
        ) {
            if (uiState !is CaptchaState.Validating) {
                SubcomposeAsyncImage(
                    modifier = Modifier
                        .fillMaxSize()
                        .rotate(degrees),
                    imageLoader = imageLoader,
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imageUrl)
                        .crossfade(true)
                        .listener(
                            onError = { _, result ->
                                LogUtil.fb(result.throwable, "loading captcha image error")
                                toastMsgBoxController.error("加载图片失败")
                                captchaViewModel.error()
                            }
                        )
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds
                )
            }
        }
        Spacer(Modifier.height(20.dp))
        LinearProgressIndicator(
            progress = { degrees / 360f },
            modifier = Modifier.width(imageSize),
            gapSize = 0.dp
        )
        Spacer(Modifier.height(20.dp))
        Text(
            text = if (uiState is CaptchaState.Retry) "加载或验证失败, 请点击确认键重新加载验证码"
            else "请用方向键旋转图片角度为正, 按确认键进行提交",
            color = if (uiState is CaptchaState.Retry) Color.Red else Color.White
        )
    }
}

suspend fun navigateAfterValid(navHostController: NavHostController) {
    withContext(Dispatchers.Main) {
        if(!navHostController.popBackStack()) {
            navHostController.navigate(NavigationItems.Home, listOf("0"))
        }
    }
}

internal class GetGuardFromRespHeaderInterceptor(
    private val captchaViewModel: CaptchaViewModel
) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val response: Response = chain.proceed(chain.request())
        captchaViewModel.ready(guard = JcyRotateCaptchaTool
            .getSetCookieValueFromHeaders(response.headers, JcyRotateCaptchaTool.COOKIE_GUARD))
        return response
    }
}