
package com.nlkprojects.kwordle.ui.views.keyboard

import android.annotation.SuppressLint
import com.nlkprojects.kwordle.R
import androidx.compose.ui.unit.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.material.Card
import androidx.compose.material.Text
import com.nlkprojects.kwordle.ui.theme.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun KeyCard(
    key: KeyType,
    size: DpSize,
    enabled: MutableState<Boolean>,
    modifier: Modifier = Modifier,
    fontFamily: FontFamily? = null,
    broadcaster: KeyUpdateBroadcaster,
    onTapCallback: (KeyType) -> Unit) {

    val colour = remember { mutableStateOf(LightGrey) }
    val colourAnimation by animateColorAsState(colour.value, tween(600), label = "")

    broadcaster.register { update ->
        when(update) {
            is KeyUpdate.Reset -> colour.value = LightGrey
            is KeyUpdate.Update -> {
                if (key.char == update.char) {
                    colour.value = if (update.isValid) Green else DarkGrey
                }
            }
        }
    }

    Card(
        modifier = modifier
            .size(size)
            .shadow(2.dp)
            .clickable(enabled = enabled.value) { onTapCallback(key) },
        backgroundColor = colourAnimation
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${key.char}",
                color = White,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                fontFamily = fontFamily
            )
        }
    }
}

@SuppressLint("ResourceType")
@Composable
fun EnterCard(
    size: DpSize,
    enabled: MutableState<Boolean>,
    modifier: Modifier = Modifier,
    onTapCallback: (KeyType) -> Unit
) {
    Card(
        modifier = modifier
            .size(size)
            .clipToBounds()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(LightGrey)
                .clickable(enabled = enabled.value) { onTapCallback(KeyType.Enter) },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.raw.enter),
                modifier = Modifier
                    .fillMaxSize(.35f)
                    .align(Alignment.Center),
                colorFilter = ColorFilter.tint(White),
                contentDescription = "Enter key"
            )
        }
    }
}

@Preview
@Composable
@SuppressLint("UnrememberedMutableState")
fun EnterCardPreview() {
    val state = mutableStateOf(true)
    EnterCard(DpSize(28.dp, 50.dp), state, Modifier) {}
}

@Suppress("unused")
private fun Path.make(
    size: Size,
    radius: Float,
    topSpacing: Dp
) {
    val w = size.width
    val h = size.height
    moveTo(w / 2, 0f)
    lineTo(w, 0f)
    arcTo(Rect(offset = Offset(w - radius, 0f), size = Size(radius, radius)), 270f, 90f, false)
    lineTo(w, h)
    arcTo(Rect(offset = Offset(w - radius, h - radius), size = Size(radius, radius)), 0f, 90f, false)
    lineTo(0f, h)
    arcTo(Rect(offset = Offset(0f, h - radius), size = Size(radius, radius)), 90f, 90f, false)
    lineTo(0f, (h / 2) + topSpacing.value)
    arcTo(Rect(offset = Offset(0f, (h / 2) + topSpacing.value), size = Size(radius, radius)), 180f, 90f, false)
    lineTo(w / 2, (h / 2) + topSpacing.value)
    arcTo(Rect(offset = Offset((w / 2) - radius, ((h / 2) + topSpacing.value) - radius), size = Size(radius, radius)), 90f, -90f, false)
    lineTo(w / 2, 0f)
    arcTo(Rect(offset = Offset(w / 2, 0f), size = Size(radius, radius)), 180f, 90f, false)
}
