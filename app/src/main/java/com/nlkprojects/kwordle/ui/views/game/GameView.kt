package com.nlkprojects.kwordle.ui.views.game

import android.annotation.SuppressLint
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import com.nlkprojects.kwordle.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import com.nlkprojects.kwordle.game.Engine
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.zIndex
import com.nlkprojects.kwordle.R
import com.nlkprojects.kwordle.game.words.DefinedWord
import com.nlkprojects.kwordle.ui.theme.*
import com.nlkprojects.kwordle.ui.views.GifImageView
import com.nlkprojects.kwordle.ui.views.TitleText
import com.nlkprojects.kwordle.ui.views.WordDefinitionView
import com.nlkprojects.kwordle.ui.views.keyboard.KeyboardView

private class EventHandler(
    val keyHandler: KeyboardEventHandler = KeyboardEventHandler(),
    val wordHandler: GameWordEventHandler = GameWordEventHandler(),
) {
    fun handle(event: Event, onLost: (DefinedWord) -> Unit, onWon: (DefinedWord) -> Unit) {
        when (event) {
            is Event.Keyboard -> keyHandler.handle(event)
            is Event.Game.Won -> onWon(event.definition)
            is Event.Game.Lost -> onLost(event.definition)
            is Event.Game.Word -> wordHandler.handle(event)
            else -> {}
        }
    }
}
private class GameWordEventHandler: WordEventBroadcaster {
    private val store = mutableListOf<(Event.Game.Word) -> Unit>()

    fun handle(event: Event.Game.Word) {
        store.onEach { it(event) }
    }
    override fun register(observer: (Event.Game.Word) -> Unit) {
        store += observer
    }
}
private class KeyboardEventHandler: KeyboardEventBroadcaster {
    private val store = mutableListOf<(Event.Keyboard) -> Unit>()

    fun handle(event: Event.Keyboard) {
        store.onEach { it(event) }
    }
    override fun register(observer: (Event.Keyboard) -> Unit) {
        store += observer
    }
}

@Composable
fun GameView(engine: Engine<*>) {
    val wonState = remember { mutableStateOf(false) }
    val hiddenState = remember { mutableStateOf(true) }
    val keyboardEnabled = remember { mutableStateOf(true) }
    val definitionState = remember { mutableStateOf<DefinedWord?>(null) }

    val eventHandler = EventHandler().apply {
        engine.register {
            handle(
                it,
                onLost = { word ->
                    wonState.value = false
                    hiddenState.value = false
                    definitionState.value = word
                    keyboardEnabled.value = false
                },
                onWon = { word ->
                    wonState.value = true
                    hiddenState.value = false
                    definitionState.value = word
                    keyboardEnabled.value = false
                }
            )
        }
    }

    Box(
        Modifier
            .fillMaxWidth()
    ) {
        OverlayView(
            Modifier
                .zIndex(if (hiddenState.value) 1f else 5f),
            wonState,
            hiddenState,
            definitionState
        ) {
            engine.reset()
            hiddenState.value = true
            keyboardEnabled.value = true
        }

        Column(
            modifier = Modifier
                .zIndex(2f)
                .fillMaxSize()
                .padding(8.dp)
                .background(MaterialTheme.colors.background),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Column(Modifier.padding(4.dp)) {
                    val length = engine.wordLength.intValue
                    for (idx in (0 until engine.maxGuesses)) {
                        WordView(idx, length, eventHandler.wordHandler)
                    }
                }
            }

            Row {
                KeyboardView(keyboardEnabled, eventHandler.keyHandler) { key ->
                    engine.handleKeyPress(key)
                }
            }
        }
    }
}

@Composable
@SuppressLint("ResourceType")
private fun OverlayView(
    modifier: Modifier,
    gameState: MutableState<Boolean>,
    hiddenState: MutableState<Boolean>,
    definitionState: MutableState<DefinedWord?>,
    onDismiss: () -> Unit
) {
    val alphaState = updateTransition(hiddenState, "").animateFloat(
        label = "",
        transitionSpec = { tween(600, easing = LinearOutSlowInEasing) }
    ) {
        if (it.value) .0f else 1f
    }

    Box(
        modifier
            .fillMaxSize()
            .offset(y = -(8.dp))
            .background(Color.Unspecified)
            .graphicsLayer { alpha = alphaState.value },
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(64f),
            modifier = Modifier
                .fillMaxWidth(.85f)
                .fillMaxHeight(.80f)
                .background(Color.Unspecified)
        ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .background(DarkerGrey),
                contentAlignment = Alignment.TopStart
            ) {
                Column(
                    Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    TitleText(
                        title = if (gameState.value) "YOU WON" else "GAME OVER",
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (gameState.value) {
                        Card(
                            shape = CircleShape,
                            modifier = Modifier
                                .offset(y = 8.dp)
                                .size(DpSize(200.dp, 200.dp)),
                        ) {
                            GifImageView(
                                R.raw.win_trophy,
                                Modifier.fillMaxSize(),
                                ContentScale.None
                            )
                        }
                    }

                    definitionState.value?.let { def ->
                        WordDefinitionView(
                            def,
                            Modifier
                                .fillMaxWidth()
                                .offset(y = 16.dp)
                                .height(IntrinsicSize.Min),
                            White,
                            Wedgewood
                        )
                    }
                }

                Button(
                    shape = RoundedCornerShape(percent = 50),
                    onClick = { onDismiss() },
                    modifier = Modifier
                        .fillMaxWidth(.85f)
                        .offset(y = -(16.dp))
                        .align(Alignment.BottomCenter),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Wedgewood)
                ) {
                    Text(text = "NEW GAME")
                }
            }
        }
    }
}
