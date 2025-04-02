package com.nlkprojects.kwordle.ui.views

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.nlkprojects.kwordle.ui.theme.White

@Composable
fun TitleText(
    title: String,
    colour: Color = White,
    fontSize: Float = 28f,
    modifier: Modifier
) {
    Text(
        text = title,
        color = colour,
        fontSize = fontSize.sp,
        modifier = modifier,
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.Bold,
    )
}
