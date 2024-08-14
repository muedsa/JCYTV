package com.muedsa.jcytv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.muedsa.compose.tv.theme.TvTheme
import com.muedsa.compose.tv.widget.Scaffold
import com.muedsa.jcytv.screens.AppNavigation
import com.muedsa.jcytv.screens.home.main.MainScreenUiState
import com.muedsa.jcytv.screens.home.main.MainScreenViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainScreenViewModel: MainScreenViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition {
            mainScreenViewModel.uiState.value is MainScreenUiState.Loading
        }
        setContent {
            TvTheme {
                Scaffold {
                    AppNavigation()
                }
            }
        }
    }
}