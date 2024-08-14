package com.muedsa.jcytv.screens.home.catalog

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.FilterChip
import androidx.tv.material3.FilterChipDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text

@OptIn(ExperimentalTvMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CatalogOptionsWidget(
    title: String,
    selectedKey: String?,
    options: Map<String, String>,
    onClick: (String, String) -> Unit = { _, _ -> }
) {
    Text(
        text = title,
        color = MaterialTheme.colorScheme.onBackground,
        style = MaterialTheme.typography.labelLarge
    )
    Spacer(modifier = Modifier.height(4.dp))
    FlowRow {
        options.forEach { (key, text) ->
            FilterChip(
                modifier = Modifier.padding(8.dp),
                selected = key == selectedKey,
                leadingIcon = if (key == selectedKey) {
                    {
                        Icon(
                            modifier = Modifier.size(FilterChipDefaults.IconSize),
                            imageVector = Icons.Outlined.Check,
                            contentDescription = "选择${text}"
                        )
                    }
                } else null,
                onClick = {
                    onClick(key, text)
                }
            ) {
                Text(text = text)
            }
        }
    }
    HorizontalDivider(modifier = Modifier.padding(bottom = 10.dp))
}