package com.muedsa.jcytv.ui.nav

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed class NavigationItems(
    val route: String,
    val args: List<NamedNavArgument> = emptyList()
) {

    data object Home : NavigationItems(
        route = "home/{tabIndex}",
        args = listOf(navArgument("tabIndex") {
            type = NavType.IntType
        })
    )

    data object Detail : NavigationItems(
        "detail/{id}", listOf(navArgument("id") {
            type = NavType.StringType
        })
    )

    data object Setting : NavigationItems("setting")

    data object RightSideDrawer : NavigationItems("right_side_drawer")
}