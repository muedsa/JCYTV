package com.muedsa.compose.tv

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.onGloballyPositioned

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

@Composable
fun Modifier.focusOnMount(itemKey: String): Modifier {
    val focusRequester = remember { FocusRequester() }
    val isInitialFocusTransferred = useLocalFocusTransferredOnLaunch()
    val lastFocusedItemPerDestination = useLocalLastFocusedItemPerDestination()
    val navHostController = useLocalNavHostController()
    val currentDestination = remember(navHostController) { navHostController.currentDestination?.route }

    return this
        .focusRequester(focusRequester)
        .onGloballyPositioned {
            val lastFocusedKey = lastFocusedItemPerDestination[currentDestination]
            if (!isInitialFocusTransferred.value && lastFocusedKey == itemKey) {
                focusRequester.requestFocus()
                isInitialFocusTransferred.value = true
            }
        }
        .onFocusChanged {
            if (it.isFocused) {
                lastFocusedItemPerDestination[currentDestination ?: ""] = itemKey
                isInitialFocusTransferred.value = true
            }
        }
}