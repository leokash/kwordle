
package com.nlkprojects.kwordle.ui.views

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable
import com.guru.fontawesomecomposelib.FaIcon
import androidx.compose.foundation.layout.Box
import com.nlkprojects.kwordle.ui.theme.White
import com.guru.fontawesomecomposelib.FaIconType

@Composable
fun FontAwesomeView(
    font: FaIconType,
    tint: Color = White,
    fontSize: Dp = 24.dp,
    modifier: Modifier
) {
    Box(modifier, Alignment.Center) {
        FaIcon(font, size = fontSize, tint = tint)
    }
}
