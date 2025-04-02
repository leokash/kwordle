package com.nlkprojects.kwordle.game

import android.util.Log
import androidx.compose.runtime.mutableIntStateOf
import kotlinx.coroutines.*
import com.nlkprojects.kwordle.Event
import com.nlkprojects.kwordle.Prefs
import com.nlkprojects.kwordle.CharGuess
import com.nlkprojects.kwordle.EventBroadcaster
import com.nlkprojects.kwordle.game.words.DictionaryProviding
import com.nlkprojects.kwordle.game.words.WordProviding
import com.nlkprojects.kwordle.ui.views.keyboard.KeyType

data class Engine<WordContext>(
    private val prefs: Prefs,
    private val guesses: Int = 6,
    private val wordContext: WordContext,
    private val coroutineScope: CoroutineScope
    ): EventBroadcaster where WordContext: WordProviding, WordContext: DictionaryProviding
{
    var maxGuesses: Int = 1
        private set

    private lateinit var game: Game
    private lateinit var word: String
    private val prefsStats = "com.nlkprojects.kwordle.sharedPref.gameStats"

    @Suppress("unused")
    val stats: List<Stat> get() = _stats
    val wordLength = mutableIntStateOf(5)

    private val _stats = mutableListOf<Stat>()
    private val observers = mutableListOf<(Event) -> Unit>()
    private val CharGuess.Result.validKeyEntry: Boolean get() {
        return this == CharGuess.Result.Success || this == CharGuess.Result.InvalidLocation
    }

    init {
        setup()
        initGame()
    }

    fun reset() {
        initGame()
        process(Event.Reset)
    }

    private fun setup() {
        maxGuesses = guesses.coerceIn(1, 6)
        prefs.getStrings(prefsStats)?.let { _stats.addAll(Stat.stats(from = it)) }
    }

    private fun initGame() {
        with(wordContext.next(wordLength.intValue)) {
            word = this
            game = Game(this, maxGuesses)
        }
    }

    fun handleKeyPress(key: KeyType) {
        coroutineScope.launch {
            Log.d(null, "didEnter: $key")
            process(game.handleKeyPress(key, wordContext))
        }
    }

    override fun register(observer: (Event) -> Unit) {
        observers += observer
    }

    private fun notify(event: Event) {
        Log.i(null, "$event")
        coroutineScope.launch(Dispatchers.Main) {
            observers.onEach { it(event) }
        }
    }

    private fun process(event: Event) {
        when (event) {
            is Event.Reset -> {
                notify(event)
                notify(Event.Keyboard.Reset)
                (0 until maxGuesses).onEach { idx -> notify(Event.Game.Word.Reset(idx)) }
            }
            is Event.Game.Won -> {
                val validatedEvent = Event.Game.Word.Validated(
                    event.guessIndex,
                    word.lowercase().mapIndexed { i, c -> CharGuess(c, i, CharGuess.Result.Success) }
                )

                notify(validatedEvent)
                validatedEvent.notifyKeyboard()

                notify(event)
                createStat(true, event.guesses, event.duration)
            }
            is Event.Game.Word.Validated -> {
                processValidated(event)
            }
            else -> {
                notify(event)
            }
        }
    }

    private fun Event.Game.Word.Validated.notifyKeyboard() {
        guesses.groupBy { it.char }.entries.forEach { (char, guesses) ->
            val keyEvent = Event.Keyboard.Validated(
                char,
                state = when(guesses.count { it.result.validKeyEntry }) {
                    0 -> Event.Keyboard.Validated.State.Error
                    else -> Event.Keyboard.Validated.State.Success
                }
            )

            notify(keyEvent)
        }
    }

    private fun processValidated(event: Event.Game.Word.Validated) {
        notify(event)
        event.notifyKeyboard()
        if (event.guessIndex == (maxGuesses - 1)) {
            val duration = System.currentTimeMillis() - game.start
            val newEvent = Event.Game.Lost(
                event.guessIndex,
                word,
                event.guesses.joinToString { "${it.char}" },
                duration,
                wordContext.definition(word)
            )

            notify(newEvent)
            createStat(false, maxGuesses, duration)
        }
    }

    private fun createStat(won: Boolean, guesses: Int, duration: Long) {
        wordContext.used(word)
        _stats + Stat(won, word.length, guesses, duration)
        prefs.putStrings(prefsStats, _stats.map { it.toString() }.toSet())
    }
}
