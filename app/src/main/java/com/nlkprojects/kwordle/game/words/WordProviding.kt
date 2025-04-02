package com.nlkprojects.kwordle.game.words

interface WordProviding {
    fun used(word: String)
    fun next(length: Int): String
}

interface DictionaryProviding {
    fun lookup(string: String): Boolean
    fun definition(string: String): DefinedWord
}
