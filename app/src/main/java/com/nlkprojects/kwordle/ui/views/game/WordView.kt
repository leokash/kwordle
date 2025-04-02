package com.nlkprojects.kwordle.ui.views.game

import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.animation.core.*
import com.nlkprojects.kwordle.CharGuess
import androidx.compose.ui.unit.IntOffset
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.nlkprojects.kwordle.ui.views.screenWidth
import com.nlkprojects.kwordle.WordEventBroadcaster

import com.nlkprojects.kwordle.Event.Game.Word as WordEvent

private enum class WordState {
    Idle, Error
}

private data class WordContext(
    val chars: List<CharContext>,
    var state: MutableState<WordState> = mutableStateOf(WordState.Idle)
)

private fun CharGuess.Result.mapToCharState(): CharState {
    return when (this) {
        CharGuess.Result.Error -> CharState.InvalidGuess
        CharGuess.Result.Success -> CharState.CorrectPosition
        CharGuess.Result.InvalidLocation -> CharState.IncorrectPosition
    }
}
private fun makeCharViewContexts(wordLength: Int): List<CharContext> {
    return buildList {
        (0 until wordLength).onEach { add(CharContext(index = it)) }
    }
}

private fun handle(event: WordEvent, context: WordContext) {
    when (event) {
        is WordEvent.Reset -> {
            context.state.value = WordState.Idle
            context.chars.onEach {
                it.char.value = ' '
                it.state.value = CharState.Idle
            }
        }
        is WordEvent.Invalid -> {
            context.state.value = WordState.Error
        }
        is WordEvent.Validated -> {
            event.guesses.onEachIndexed { idx, guess ->
                context.chars[idx].state.value = guess.result.mapToCharState()
            }
        }
        is WordEvent.KeyPressed -> {
            context.chars[event.index].char.value = event.char
        }
    }
}

@Composable
fun WordView(index: Int, characters: Int = 5, gameEventBroadcaster: WordEventBroadcaster) {
    val word = remember { WordContext(makeCharViewContexts(characters)) }
    gameEventBroadcaster.register { event ->
        if (event.guessIndex == index) {
            handle(event, word)
        }
    }

    val offset = animateIntOffsetAsState(
        label = "WordView x offset animation",
        targetValue = if (word.state.value == WordState.Error) IntOffset(8, 0) else IntOffset.Zero,
        animationSpec = RepeatableSpec(2, keyframes {
            durationMillis = 100
            IntOffset(-8, 0) at 0 using LinearEasing
            IntOffset(0, 0) at 50 using LinearEasing
            IntOffset(8, 0) at 100 using LinearEasing
        },
        repeatMode = RepeatMode.Reverse),
        finishedListener = { word.state.value = WordState.Idle }
    )

    Row(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
            .offset(offset.value.x.dp, offset.value.y.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        val size = screenWidth(multiplier = .85f) / characters
        word.chars.onEach {
            CharView(size = size, context = it)
        }
    }
}
