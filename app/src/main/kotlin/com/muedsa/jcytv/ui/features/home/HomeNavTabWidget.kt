package com.muedsa.jcytv.ui.features.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Tab
import androidx.tv.material3.TabDefaults
import androidx.tv.material3.TabRow
import androidx.tv.material3.TabRowDefaults
import androidx.tv.material3.Text
import com.muedsa.compose.tv.widget.NotImplementScreen
import com.muedsa.compose.tv.widget.ScreenBackgroundType
import com.muedsa.jcytv.ui.features.home.favorites.FavoritesScreen
import com.muedsa.jcytv.ui.features.home.main.MainScreen
import com.muedsa.jcytv.ui.features.home.search.SearchScreen
import com.muedsa.jcytv.viewmodel.HomePageViewModel
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

val tabs: Array<HomeNavTab> = HomeNavTab.entries.toTypedArray()

@OptIn(ExperimentalTvMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun HomeNavTabWidget(
    tabIndex: Int = 0,
    homePageViewModel: HomePageViewModel
) {
    val backgroundState = LocalHomeScreenBackgroundState.current
    var focusedTabIndex by rememberSaveable { mutableIntStateOf(tabIndex) }
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(focusedTabIndex) }

    var tabPanelIndex by remember { mutableIntStateOf(selectedTabIndex) }

    LaunchedEffect(selectedTabIndex) {
        delay(150.milliseconds)
        tabPanelIndex = selectedTabIndex
    }

    Column {
        TabRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, bottom = 24.dp)
                .focusRestorer(),
            selectedTabIndex = selectedTabIndex,
            indicator = { tabPositions, doesTabRowHaveFocus ->
                // FocusedTab's indicator
                TabRowDefaults.PillIndicator(
                    currentTabPosition = tabPositions[focusedTabIndex],
                    doesTabRowHaveFocus = doesTabRowHaveFocus,
                    activeColor = MaterialTheme.colorScheme.inverseSurface.copy(alpha = 0.4f),
                    inactiveColor = Color.Transparent
                )

                // SelectedTab's indicator
                TabRowDefaults.PillIndicator(
                    currentTabPosition = tabPositions[selectedTabIndex],
                    doesTabRowHaveFocus = doesTabRowHaveFocus
                )
            }
        ) {
            tabs.forEachIndexed { index, tab ->
                Tab(
                    selected = selectedTabIndex == index,
                    onFocus = { focusedTabIndex = index },
                    onClick = {
                        if (selectedTabIndex != index) {
                            backgroundState.url = null
                            backgroundState.type = ScreenBackgroundType.BLUR
                            selectedTabIndex = index
                        }
                    },
                    colors = TabDefaults.pillIndicatorTabColors(
                        selectedContentColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text(
                        tab.title,
                        fontSize = MaterialTheme.typography.labelLarge.fontSize,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }
            }
        }
        HomeContent(
            tabIndex = tabPanelIndex,
            homePageViewModel = homePageViewModel
        )
    }
}

@Composable
fun HomeContent(
    tabIndex: Int,
    homePageViewModel: HomePageViewModel
) {
    val tab = tabs[tabIndex]

    when(tab) {
        HomeNavTab.Main -> {
            MainScreen(
                viewModel = homePageViewModel
            )
        }
        HomeNavTab.Catalog -> NotImplementScreen()
        HomeNavTab.Rank -> NotImplementScreen()
        HomeNavTab.Search -> SearchScreen()
        HomeNavTab.Favorites -> FavoritesScreen()
    }
}