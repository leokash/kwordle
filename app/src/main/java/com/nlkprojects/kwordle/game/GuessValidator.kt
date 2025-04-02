package com.nlkprojects.kwordle.game

import com.nlkprojects.kwordle.CharGuess
import com.nlkprojects.kwordle.CharGuess.Result as GuessResult

import com.nlkprojects.kwordle.game.words.DictionaryProviding

sealed class ValidationResult {
    data object Valid : ValidationResult()
    data object Invalid : ValidationResult()
    data class Validated(val guesses: List<CharGuess>) : ValidationResult()
}

class GuessValidator(private val word: String) {
    private class Position(val index: Int, var occupied: Boolean)

    private fun validate(guess: String): List<CharGuess> {
        val positions: Map<Char, MutableList<Position>> = buildMap {
            word.withIndex().forEach { (idx, char) ->
                getOrPut(char) { mutableListOf() }.add(Position(idx, char == guess[idx]))
            }
        }

        return guess.mapIndexed { idx, char ->
            CharGuess(char, idx, when (val list = positions[char]) {
                null -> GuessResult.Error
                else -> when {
                    list.firstOrNull { it.index == idx } != null -> GuessResult.Success
                    list.firstOrNull { !it.occupied }?.also { it.occupied = true } != null -> GuessResult.InvalidLocation
                    else -> GuessResult.Error
                }
            })
        }
    }

    fun validate(guess: String, dictionary: DictionaryProviding): ValidationResult {
        return when {
            guess == word -> ValidationResult.Valid
            guess.length != word.length || !dictionary.lookup(guess) -> ValidationResult.Invalid
            else ->  ValidationResult.Validated(validate(guess))
        }
    }
}
