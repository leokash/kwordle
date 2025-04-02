
package com.nlkprojects.kwordle.game.words

import com.nlkprojects.kwordle.R
import com.nlkprojects.kwordle.Prefs
import android.content.res.Resources
import com.nlkprojects.kwordle.utils.onSuccess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WordProvider(
    resource: Resources,
    private val prefs: Prefs,
    private val coroutineScope: CoroutineScope,
    private val definitionFetcher: DefinitionFetching
): WordProviding, DictionaryProviding {
    private val prefsUsed = "com.nlkprojects.kwordle.sharedPref.usedWords"
    private val syllableSeparator = "·"

    private val used = mutableSetOf<String>()
    private val dtoMap = mutableMapOf<String, WordDto>()
    private val wordsMap = resource.openRawResource(R.raw.words_definitions)
        .bufferedReader()
        .use { it.readLines() }
        .mapNotNull { makeDefinedWordFromString(it.trim()) }

    private var syllables = resource.openRawResource(R.raw.words_syllabified)
        .bufferedReader()
        .use { it.readLines() }
        .associate { val word = it.trim(); word.replace(";", "") to word.replace(";", syllableSeparator) }

    init {
        prefs.getStrings(prefsUsed)?.let { used.addAll(it) }
    }

    override fun used(word: String) {
        used += word
        prefs.putStrings(prefsUsed, used)
    }

    override fun next(length: Int): String {
        fun clearUsed() {
            used.clear()
            prefs.putStrings(prefsUsed, emptySet())
        }
        fun filteredWords(): List<String> {
            return wordsMap
                .mapNotNull { (_, word, _, _) ->
                    if (word.length == length && word !in used && syllables[word] != null) word else null
                }
        }

        return with(filteredWords()) {
            if (isEmpty()) { clearUsed(); next(length) } else shuffled().first().also {
                coroutineScope.launch(Dispatchers.IO) {
                    definitionFetcher.fetchDefinition(it).onSuccess { dto ->
                        dtoMap[it] = dto
                    }
                }
            }
        }
    }

    override fun lookup(string: String): Boolean {
        return wordsMap.any { it.value.equals(string, true) }
    }

    override fun definition(string: String): DefinedWord {
        return wordsMap
            .first { it.value == string }
            .copy(phonetic = DefinedWord.Phonetic(dtoMap[string]?.ipa, syllables[string] ?: ""))
    }

    private fun makeDefinedWordFromString(string: String): DefinedWord? {
        val word = string.substringBefore('\t')
        val typeString = string.substringAfter("[", "")
        return if (typeString.isEmpty()) null else DefinedWord(
            DefinedWord.type(typeString),
            word.trim().lowercase(),
            null,
            string.replace(word + '\t', "")
        )
    }
}
