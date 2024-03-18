package com.muedsa.compose.tv.theme

import androidx.compose.runtime.Composable
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme


@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TvTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = TvDarkColorScheme,
        content = content
    )
}