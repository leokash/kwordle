package com.nlkprojects.kwordle.ui.views

import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.DpSize
import com.nlkprojects.kwordle.ui.theme.*
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.MutableState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.ui.draw.shadow
import com.guru.fontawesomecomposelib.FaIcons
import com.guru.fontawesomecomposelib.FaIconType
import com.nlkprojects.kwordle.Routes

private val iconSize = DpSize(36.dp, 36.dp)

@Composable
fun TitleButton(
    font: FaIconType,
    enabled: MutableState<Boolean>,
    modifier: Modifier = Modifier,
    onClickAction: () -> Unit
) {
    FontAwesomeView(
        font = font,
        tint = White,
        fontSize = 28.dp,
        modifier = modifier
            .size(iconSize)
            .background(Color.Unspecified)
            .clickable(enabled.value, onClick = onClickAction)
    )
}

@Composable
fun TitleView(
    title: String,
    enabled: MutableState<Boolean>,
    onAction: (Routes) -> Unit) {
    Box(
        Modifier
            .height(64.dp)
            .shadow(elevation = 1.dp, spotColor = Color.Black)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        TitleText(title, modifier = Modifier.align(Alignment.Center))
        Row(
            Modifier
                .absolutePadding(left = 8.dp)
                .align(Alignment.CenterStart)
        ) {
            TitleButton(FaIcons.Cog, enabled) { onAction(Routes.Settings) }
        }
    }
}
