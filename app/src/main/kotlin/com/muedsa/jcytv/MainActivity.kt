package com.muedsa.jcytv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.muedsa.compose.tv.theme.TvTheme
import com.muedsa.compose.tv.widget.Scaffold
import com.muedsa.jcytv.ui.nav.AppNavigation
import com.muedsa.jcytv.viewmodel.HomePageViewModel
import com.muedsa.model.LazyType
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val homePageViewModel: HomePageViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition {
            homePageViewModel.homeRowsSF.value.type == LazyType.LOADING
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