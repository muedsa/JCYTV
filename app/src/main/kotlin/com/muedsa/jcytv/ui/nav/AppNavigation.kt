package com.muedsa.jcytv.ui.nav

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import com.muedsa.compose.tv.widget.FullWidthDialogProperties
import com.muedsa.compose.tv.widget.LocalRightSideDrawerState
import com.muedsa.compose.tv.widget.RightSideDrawerWithNavDrawerContent
import com.muedsa.compose.tv.widget.RightSideDrawerWithNavState
import com.muedsa.jcytv.ui.features.detail.AnimeDetailScreen
import com.muedsa.jcytv.ui.features.home.HomeNavScreen
import com.muedsa.jcytv.ui.features.setting.AppSettingScreen

val LocalAppNavController = compositionLocalOf<NavHostController> {
    error("LocalAppNavController not init")
}

@Composable
fun AppNavigation(navController: NavHostController) {
    val viewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) {
        "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
    }

    val rightSideDrawerState = RightSideDrawerWithNavState(navController, NavigationItems.RightSideDrawer.route)

    CompositionLocalProvider(
        LocalAppNavController provides navController,
        LocalRightSideDrawerState provides rightSideDrawerState
    ) {
        NavHost(
            navController = navController,
            startDestination = buildJumpRoute(NavigationItems.Home, listOf("0"))
        ) {

            composable(
                route = NavigationItems.Home.route,
                arguments = NavigationItems.Home.args
            ) {
                HomeNavScreen(
                    tabIndex = checkNotNull(it.arguments?.getInt("tabIndex")),
                    homePageViewModel = hiltViewModel(viewModelStoreOwner),
                )
            }

            composable(
                route = NavigationItems.Detail.route,
                arguments = NavigationItems.Detail.args
            ) {
                AnimeDetailScreen()
            }

            dialog(
                route = NavigationItems.Setting.route,
                dialogProperties = FullWidthDialogProperties()
            ) {
                AppSettingScreen()
            }

            dialog(
                route = NavigationItems.RightSideDrawer.route,
                dialogProperties = FullWidthDialogProperties()
            ) {
                RightSideDrawerWithNavDrawerContent(
                    state = rightSideDrawerState
                )
            }
        }
    }
}

fun buildJumpRoute(
    navItem: NavigationItems,
    pathParams: List<String>?
): String {
    var route = navItem.route
    if (navItem.args.isNotEmpty()) {
        checkNotNull(pathParams)
        check(pathParams.size == navItem.args.size)
        for (i in 0 until navItem.args.size) {
            route = route.replace("{${navItem.args[i].name}}", pathParams[i])
        }
    }
    return route
}

fun NavHostController.navigate(
    navItem: NavigationItems,
    pathParams: List<String>? = null
) {
    navigate(buildJumpRoute(navItem, pathParams))
}
