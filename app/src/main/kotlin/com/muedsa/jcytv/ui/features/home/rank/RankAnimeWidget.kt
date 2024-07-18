package com.muedsa.jcytv.ui.features.home.rank

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Card
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.muedsa.compose.tv.conditional
import com.muedsa.jcytv.model.JcyRankVideoInfo

@Composable
fun RankAnimeWidget(
    model: JcyRankVideoInfo,
    onClick: () -> Unit = {}
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        onClick = onClick,
        interactionSource = interactionSource,
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                modifier = Modifier
                    .padding(8.dp)
                    .wrapContentWidth(Alignment.Start),
                text = "${model.index}. ",
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleMedium,
            )
            Modifier
            Text(
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
                    .wrapContentWidth(Alignment.Start)
                    .conditional(isFocused) { basicMarquee() },
                text = model.title,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleMedium,
            )
            if (model.hotNum > 0) {
                Text(
                    modifier = Modifier
                        .padding(8.dp)
                        .wrapContentWidth(Alignment.End),
                    text = "\uD83D\uDD25${model.hotNum}",
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }

    }
}