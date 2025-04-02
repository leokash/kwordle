
package com.nlkprojects.kwordle.ui.views.game

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.nlkprojects.kwordle.ui.theme.*
import com.nlkprojects.kwordle.ui.theme.LightGrey

enum class CharState {
    Idle, InvalidGuess, CorrectPosition, IncorrectPosition
}

data class CharContext(
    var char: MutableState<Char> = mutableStateOf(' '),
    val index: Int,
    var state: MutableState<CharState> = mutableStateOf(CharState.Idle)
)

@Composable
fun CharView(size: Dp, context: CharContext) {
    val transition = updateTransitionData(context.state.value)
    Box(
        Modifier
            .size(size)
            .background(transition.colour)
            .graphicsLayer {
                rotationY = transition.rotation
                cameraDistance = 8 * density
            }
            .border(2.dp, transition.borderColour, RectangleShape),
        contentAlignment = Alignment.Center
    ) {
        val text = remember { context.char }
        Text(
            text = "${text.value}".uppercase(),
            color = transition.textColour,
            fontSize = 30.sp,
            modifier = Modifier.graphicsLayer { rotationY = transition.rotation },
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
        )
    }
}

private class TransitionData(
    colour: State<Color>,
    rotation: State<Float>,
    textColour: State<Color>,
    borderColour: State<Color>
) {
    val colour by colour
    val rotation by rotation
    val textColour by textColour
    val borderColour by borderColour
}

@Composable
private fun updateTransitionData(state: CharState, duration: Int = 600): TransitionData {
    val transition = updateTransition(state, label = "")
    val colour = transition.animateColor(
        transitionSpec = { tween(duration, easing = LinearOutSlowInEasing) },
        label = ""
    ) {
        when (it) {
            CharState.Idle -> Color.Unspecified
            CharState.InvalidGuess -> LightGrey
            CharState.CorrectPosition -> Green
            CharState.IncorrectPosition -> Yellow
        }
    }
    val rotation = transition.animateFloat(
        transitionSpec = { tween(duration, easing = LinearOutSlowInEasing) },
        label = ""
    ) {
       if (it == CharState.Idle) 0f else 180f
    }
    val textColour = transition.animateColor(
        transitionSpec = { tween(duration / 2, easing = LinearOutSlowInEasing, delayMillis = duration / 2) },
        label = ""
    ) {
        if (it == CharState.Idle) LightGrey else White
    }
    val borderColour = transition.animateColor(
        transitionSpec = { tween(duration / 2, easing = LinearOutSlowInEasing, delayMillis = duration / 2) },
        label = ""
    ) {
        if (it == CharState.Idle) LightGrey else Color.Unspecified
    }

    return remember { TransitionData(colour, rotation, textColour, borderColour) }
}
