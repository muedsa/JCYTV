package com.muedsa.jcytv.screens

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import com.muedsa.compose.tv.LocalNavHostControllerProvider
import com.muedsa.compose.tv.LocalRightSideDrawerControllerProvider
import com.muedsa.compose.tv.widget.FullWidthDialogProperties
import com.muedsa.compose.tv.widget.RightSideDrawerWithNavController
import com.muedsa.compose.tv.widget.RightSideDrawerWithNavDrawerContent
import com.muedsa.jcytv.screens.detail.AnimeDetailScreen
import com.muedsa.jcytv.screens.home.HomeNavScreen
import com.muedsa.jcytv.screens.setting.AppSettingScreen

@Composable
fun AppNavigation() {
    val viewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) {
        "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
    }
    val navController = rememberNavController()
    val drawerController = RightSideDrawerWithNavController(navController, NavigationItems.RightSideDrawer.route)

    LocalNavHostControllerProvider(navController) {
        LocalRightSideDrawerControllerProvider(drawerController) {
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
                        mainScreenViewModel = hiltViewModel(viewModelStoreOwner),
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
                        controller = drawerController
                    )
                }
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

fun NavHostController.nav(
    navItem: NavigationItems,
    pathParams: List<String>? = null
) {
    navigate(buildJumpRoute(navItem, pathParams))
}
