package com.muedsa.jcytv.screens.home.rank

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.muedsa.compose.tv.theme.ScreenPaddingLeft
import com.muedsa.compose.tv.useLocalNavHostController
import com.muedsa.jcytv.model.JcyRankList
import com.muedsa.jcytv.screens.NavigationItems
import com.muedsa.jcytv.screens.navigate
import com.muedsa.uitl.LogUtil
import kotlin.math.min

@Composable
fun RankWidget(
    list: List<JcyRankList>
) {
    val navController = useLocalNavHostController()

    Column(modifier = Modifier.padding(start = ScreenPaddingLeft)) {
        val ranks = list.subList(0, min(3, list.size))
        Row {
            ranks.forEachIndexed { index, rank ->
                Column(
                    modifier = Modifier
                        .padding(top = 10.dp, bottom = 10.dp, end = 10.dp)
                        .weight(1f)
                        .testTag("rankScreen_column_$index")
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = rank.title,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.titleLarge
                    )
                    LazyColumn {
                        items(rank.list) {
                            RankItemWidget(
                                model = it,
                                onClick = {
                                    LogUtil.d("Click $it")
                                    navController.navigate(
                                        NavigationItems.Detail,
                                        listOf(it.id.toString())
                                    )
                                }
                            )
                        }
                        item {
                            Spacer(modifier = Modifier.height(100.dp))
                        }
                    }
                }
            }
        }
    }
}