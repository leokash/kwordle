package com.nlkprojects.kwordle.game

import com.nlkprojects.kwordle.CharGuess
import com.nlkprojects.kwordle.game.words.DefinedWord
import com.nlkprojects.kwordle.game.words.DictionaryProviding
import org.junit.Test

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.isA
import org.hamcrest.Matchers.nullValue
import org.junit.Before

class DictionaryMock: DictionaryProviding {
    var lookupCallCount: Int = 0
        private set
    var definitionCallCount: Int = 0
        private set

    var lookupValueToReturn: Boolean? = null

    var givenLookupWord: String? = null
        private set
    var givenDefinitionForWord: String? = null
        private set

    override fun lookup(string: String): Boolean {
        lookupCallCount += 1
        givenLookupWord = string
        return lookupValueToReturn ?: false
    }

    override fun definition(string: String): DefinedWord {
        definitionCallCount += 1
        givenDefinitionForWord = string
        return DefinedWord(DefinedWord.Type.Noun, "", null, "")
    }
}

class GuessValidatorTest {
    private lateinit var dictionary: DictionaryMock

    @Before
    fun setup() {
        dictionary = DictionaryMock()
        dictionary.lookupValueToReturn = true
    }

    @Test
    fun validationWithEmptyString() {
        val guess = ""
        val result = GuessValidator("trial").validate(guess, dictionary)

        assertThat(dictionary.lookupCallCount, equalTo(0))
        assertThat(dictionary.givenLookupWord, nullValue())
        assertThat(result, isA(ValidationResult.Invalid::class.java))
    }

    @Test
    fun validationWhenGuessNotInDictionary() {
        val guess = "zaevo"
        dictionary.lookupValueToReturn = false
        val result = GuessValidator("trial").validate(guess, dictionary)

        assertThat(dictionary.lookupCallCount, equalTo(1))
        assertThat(dictionary.givenLookupWord, equalTo(guess))
        assertThat(result, isA(ValidationResult.Invalid::class.java))
    }

    @Test
    fun validationWithRightWordShouldSucceed() {
        val guess = "trial"
        val result = GuessValidator("trial").validate(guess, dictionary)

        assertThat(dictionary.lookupCallCount, equalTo(0))
        assertThat(result, isA(ValidationResult.Valid::class.java))
    }

    @Test
    fun validationWithMismatchOfWordLength() {
        val guess = "tria"
        val result = GuessValidator("trial").validate(guess, dictionary)

        assertThat(dictionary.lookupCallCount, equalTo(0))
        assertThat(dictionary.givenLookupWord, nullValue())
        assertThat(result, isA(ValidationResult.Invalid::class.java))
    }

    @Test
    fun validateWhenTwoCharsMatch() {
        val guess = "woods"
        val result = GuessValidator("soups")
            .validate(guess, dictionary) as ValidationResult.Validated

        assertThat(result.guesses.size, equalTo(5))
        assertThat(dictionary.lookupCallCount, equalTo(1))
        assertThat(dictionary.givenLookupWord, equalTo(guess))
        assertThat(result.guesses[0], equalTo(CharGuess('w', 0, CharGuess.Result.Error)))
        assertThat(result.guesses[1], equalTo(CharGuess('o', 1, CharGuess.Result.Success)))
        assertThat(result.guesses[2], equalTo(CharGuess('o', 2, CharGuess.Result.Error)))
        assertThat(result.guesses[3], equalTo(CharGuess('d', 3, CharGuess.Result.Error)))
        assertThat(result.guesses[4], equalTo(CharGuess('s', 4, CharGuess.Result.Success)))
    }

    @Test
    fun validateConsecutively() {
        val validator = GuessValidator("soups")
        val result1 = validator.validate("guess", dictionary) as ValidationResult.Validated
        val result2 = validator.validate("about", dictionary) as ValidationResult.Validated

        assertThat(result1.guesses[0], equalTo(CharGuess('g', 0, CharGuess.Result.Error)))
        assertThat(result1.guesses[1], equalTo(CharGuess('u', 1, CharGuess.Result.InvalidLocation)))
        assertThat(result1.guesses[2], equalTo(CharGuess('e', 2, CharGuess.Result.Error)))
        assertThat(result1.guesses[3], equalTo(CharGuess('s', 3, CharGuess.Result.InvalidLocation)))
        assertThat(result1.guesses[4], equalTo(CharGuess('s', 4, CharGuess.Result.Success)))

        assertThat(result2.guesses[0], equalTo(CharGuess('a', 0, CharGuess.Result.Error)))
        assertThat(result2.guesses[1], equalTo(CharGuess('b', 1, CharGuess.Result.Error)))
        assertThat(result2.guesses[2], equalTo(CharGuess('o', 2, CharGuess.Result.InvalidLocation)))
        assertThat(result2.guesses[3], equalTo(CharGuess('u', 3, CharGuess.Result.InvalidLocation)))
        assertThat(result2.guesses[4], equalTo(CharGuess('t', 4, CharGuess.Result.Error)))
    }

    @Test
    fun validateWhenNoCharsMatch() {
        val result = GuessValidator("soups")
            .validate("exact", dictionary) as ValidationResult.Validated

        assertThat(result.guesses[0], equalTo(CharGuess('e', 0, CharGuess.Result.Error)))
        assertThat(result.guesses[1], equalTo(CharGuess('x', 1, CharGuess.Result.Error)))
        assertThat(result.guesses[2], equalTo(CharGuess('a', 2, CharGuess.Result.Error)))
        assertThat(result.guesses[3], equalTo(CharGuess('c', 3, CharGuess.Result.Error)))
        assertThat(result.guesses[4], equalTo(CharGuess('t', 4, CharGuess.Result.Error)))
    }

    @Test
    fun validateWhenWordHasSingleCharButGuessHasMultipleOfCharValue() {
        val guess = "meter"
        val result = GuessValidator("suave")
            .validate(guess, dictionary) as ValidationResult.Validated

        assertThat(result.guesses[0], equalTo(CharGuess('m', 0, CharGuess.Result.Error)))
        assertThat(result.guesses[1], equalTo(CharGuess('e', 1, CharGuess.Result.InvalidLocation)))
        assertThat(result.guesses[2], equalTo(CharGuess('t', 2, CharGuess.Result.Error)))
        assertThat(result.guesses[3], equalTo(CharGuess('e', 3, CharGuess.Result.Error)))
        assertThat(result.guesses[4], equalTo(CharGuess('r', 4, CharGuess.Result.Error)))
    }

    @Test
    fun validateWhenWordHasSingleCharButGuessHasMultipleOfCharAndOneCharInRightPlace() {
        val guess = "melee"
        val result = GuessValidator("suave")
            .validate(guess, dictionary) as ValidationResult.Validated

        assertThat(result.guesses[0], equalTo(CharGuess('m', 0, CharGuess.Result.Error)))
        assertThat(result.guesses[1], equalTo(CharGuess('e', 1, CharGuess.Result.Error)))
        assertThat(result.guesses[2], equalTo(CharGuess('l', 2, CharGuess.Result.Error)))
        assertThat(result.guesses[3], equalTo(CharGuess('e', 3, CharGuess.Result.Error)))
        assertThat(result.guesses[4], equalTo(CharGuess('e', 4, CharGuess.Result.Success)))
    }

    @Test
    fun validateWhenWordHasTwoOfSameCharButGuessHasMultipleOfCharAndOneCharInRightPlace() {
        val guess = "melee"
        val result = GuessValidator("eater")
            .validate(guess, dictionary) as ValidationResult.Validated

        assertThat(result.guesses[0], equalTo(CharGuess('m', 0, CharGuess.Result.Error)))
        assertThat(result.guesses[1], equalTo(CharGuess('e', 1, CharGuess.Result.InvalidLocation)))
        assertThat(result.guesses[2], equalTo(CharGuess('l', 2, CharGuess.Result.Error)))
        assertThat(result.guesses[3], equalTo(CharGuess('e', 3, CharGuess.Result.Success)))
        assertThat(result.guesses[4], equalTo(CharGuess('e', 4, CharGuess.Result.Error)))
    }

    @Test
    fun validateWhenWordHasTwoOfSameCharButGuessHasMultipleOfCharAndArePlacedBefore() {
        val guess = "eerie"
        val result = GuessValidator("allee")
            .validate(guess, dictionary) as ValidationResult.Validated

        assertThat(result.guesses[0], equalTo(CharGuess('e', 0, CharGuess.Result.InvalidLocation)))
        assertThat(result.guesses[1], equalTo(CharGuess('e', 1, CharGuess.Result.Error)))
        assertThat(result.guesses[2], equalTo(CharGuess('r', 2, CharGuess.Result.Error)))
        assertThat(result.guesses[3], equalTo(CharGuess('i', 3, CharGuess.Result.Error)))
        assertThat(result.guesses[4], equalTo(CharGuess('e', 4, CharGuess.Result.Success)))
    }

    @Test
    fun validateWhenWordHasThreeOfSameCharButGuessHasMultipleOfCharAndTwoCharsInRightPlace() {
        val guess = "eerie"
        val result = GuessValidator("melee")
            .validate(guess, dictionary) as ValidationResult.Validated

        assertThat(result.guesses[0], equalTo(CharGuess('e', 0, CharGuess.Result.InvalidLocation)))
        assertThat(result.guesses[1], equalTo(CharGuess('e', 1, CharGuess.Result.Success)))
        assertThat(result.guesses[2], equalTo(CharGuess('r', 2, CharGuess.Result.Error)))
        assertThat(result.guesses[3], equalTo(CharGuess('i', 3, CharGuess.Result.Error)))
        assertThat(result.guesses[4], equalTo(CharGuess('e', 4, CharGuess.Result.Success)))
    }

    @Test
    fun validateWhenWordHasThreeOfSameCharButGuessHasAlsoHasThreeOfSameCharAndTwoCharsInRightPlace() {
        val guess = "geese"
        val result = GuessValidator("melee")
            .validate(guess, dictionary) as ValidationResult.Validated

        assertThat(result.guesses[0], equalTo(CharGuess('g', 0, CharGuess.Result.Error)))
        assertThat(result.guesses[1], equalTo(CharGuess('e', 1, CharGuess.Result.Success)))
        assertThat(result.guesses[2], equalTo(CharGuess('e', 2, CharGuess.Result.InvalidLocation)))
        assertThat(result.guesses[3], equalTo(CharGuess('s', 3, CharGuess.Result.Error)))
        assertThat(result.guesses[4], equalTo(CharGuess('e', 4, CharGuess.Result.Success)))
    }

    @Test
    fun validateWhenAllCharsAreValidButInWrongPlace() {
        val guess = "deere"
        val result = GuessValidator("reede")
            .validate(guess, dictionary) as ValidationResult.Validated

        assertThat(result.guesses[0], equalTo(CharGuess('d', 0, CharGuess.Result.InvalidLocation)))
        assertThat(result.guesses[1], equalTo(CharGuess('e', 1, CharGuess.Result.Success)))
        assertThat(result.guesses[2], equalTo(CharGuess('e', 2, CharGuess.Result.Success)))
        assertThat(result.guesses[3], equalTo(CharGuess('r', 3, CharGuess.Result.InvalidLocation)))
        assertThat(result.guesses[4], equalTo(CharGuess('e', 4, CharGuess.Result.Success)))
    }
}
