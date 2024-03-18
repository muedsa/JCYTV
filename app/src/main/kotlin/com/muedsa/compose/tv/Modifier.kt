package com.muedsa.compose.tv

import androidx.compose.ui.Modifier

inline fun Modifier.conditional(
    condition: Boolean,
    ifTrue: Modifier.() -> Modifier,
): Modifier = if (condition) {
    then(ifTrue(Modifier))
} else {
    this
}

inline fun Modifier.conditional(
    condition: Boolean,
    ifTrue: Modifier.() -> Modifier,
    ifFalse: Modifier.() -> Modifier = { this },
): Modifier = if (condition) {
    then(ifTrue(Modifier))
} else {
    then(ifFalse(Modifier))
}