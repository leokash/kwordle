package com.nlkprojects.kwordle.ui.views

import androidx.compose.ui.unit.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration

@Composable
fun screenWidth(multiplier: Float = 1f): Dp {
    return (LocalConfiguration.current.screenWidthDp * multiplier).dp
}
