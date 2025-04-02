
package com.nlkprojects.kwordle.game

import com.nlkprojects.kwordle.Event
import com.nlkprojects.kwordle.Event.Game.Won as GameWon
import com.nlkprojects.kwordle.Event.Game.Lost as GameOver
import com.nlkprojects.kwordle.Event.Game.Word.Invalid as InvalidWord
import com.nlkprojects.kwordle.Event.Game.Word.Validated as ValidatedWord
import com.nlkprojects.kwordle.game.ValidationResult.Invalid
import com.nlkprojects.kwordle.game.ValidationResult.Valid
import com.nlkprojects.kwordle.game.ValidationResult.Validated
import com.nlkprojects.kwordle.game.words.DictionaryProviding
import com.nlkprojects.kwordle.ui.views.keyboard.KeyType
import com.nlkprojects.kwordle.ui.views.keyboard.KeyType.Delete
import com.nlkprojects.kwordle.ui.views.keyboard.KeyType.Enter
import com.nlkprojects.kwordle.ui.views.keyboard.KeyType.Value

data class Game(
    private val word: String,
    private val maxGuesses: Int
) {
    val start: Long = System.currentTimeMillis()
    private val length = word.length
    private val charRange = (0 until length)
    private val guessValidator = GuessValidator(word.lowercase().trim())

    private val guess: String get() = guessStore.joinToString("").lowercase()

    private var cursor = 0
    private var guessIndex = 0
    private var guessStore = CharArray(length) { ' ' }

    private fun update() {
        cursor = 0
        guessIndex++
        guessStore = CharArray(length) { ' ' }
    }
    private fun incCursor() {
        if (cursor < length) { cursor++ }
    }
    private fun decCursor() {
        if (cursor > 0) { cursor-- }
    }
    private fun backspace(): Event {
        decCursor()
        guessStore[cursor] = EMPTY
        return Event.Game.Word.KeyPressed(EMPTY, cursor, guessIndex)
    }
    private fun receivedChar(char: Char): Event {
        if (cursor in charRange) {
            guessStore[cursor] = char
            return Event.Game.Word.KeyPressed(char, cursor, guessIndex).also { incCursor() }
        }

        return Event.Idle
    }
    private fun validate(dictionary: DictionaryProviding): Event {
        if (guessIndex >= maxGuesses)
            return GameOver(guessIndex, word, guess, System.currentTimeMillis() - start, dictionary.definition(word))

        return guessValidator.validate(guess, dictionary).let { result ->
            when (result) {
                is Valid -> GameWon(guessIndex + 1, System.currentTimeMillis() - start, dictionary.definition(word))
                is Invalid -> InvalidWord(guessIndex)
                is Validated -> ValidatedWord(guessIndex, result.guesses).also { update() }
            }
        }
    }

    fun handleKeyPress(key: KeyType, dictionary: DictionaryProviding): Event {
        return when (key) {
            is Value -> receivedChar(key.char)
            is Enter -> validate(dictionary)
            is Delete -> backspace()
        }
    }

    companion object {
        private const val EMPTY: Char = ' '
    }
}
