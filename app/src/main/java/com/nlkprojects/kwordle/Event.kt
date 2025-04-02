
package com.nlkprojects.kwordle

import com.nlkprojects.kwordle.game.words.DefinedWord

data class CharGuess(
    val char: Char,
    val index: Int,
    val result: Result
) {
    enum class Result {
        Error, Success, InvalidLocation
    }
}

sealed class Event {
    data object Idle: Event()
    data object Reset: Event()
    sealed class Keyboard: Event() {
        data object Reset: Keyboard()
        data class Validated(val char: Char, val state: State): Keyboard() {
            enum class State {
                Error, Success
            }
        }
    }
    sealed class Game(val guessIndex: Int): Event() {
        data class Won(val guesses: Int, val duration: Long, val definition: DefinedWord): Game(guesses - 1)
        data class Lost(private val index: Int, val word: String, val guess: String, val duration: Long, val definition: DefinedWord): Game(index)

        sealed class Word(index: Int): Game(index) {
            data class Reset(private val index: Int): Word(index)
            data class Invalid(private val index: Int): Word(index)
            data class Validated(private val index: Int, val guesses: List<CharGuess>): Word(index)
            data class KeyPressed(val char: Char, val index: Int, private val position: Int): Word(position)
        }
    }
}

interface EventBroadcaster {
    fun register(observer: (Event) -> Unit)
}
interface WordEventBroadcaster {
    fun register(observer: (Event.Game.Word) -> Unit)
}
interface KeyboardEventBroadcaster {
    fun register(observer: (Event.Keyboard) -> Unit)
}
