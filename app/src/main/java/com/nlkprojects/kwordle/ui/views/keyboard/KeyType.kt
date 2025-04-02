package com.nlkprojects.kwordle.ui.views.keyboard

sealed class KeyType {
    abstract val char: Char

    data class Value(override val char: Char): KeyType()
    data object Enter: KeyType() { override val char: Char get() = '\uE331' }
    data object Delete: KeyType() { override val char: Char get() = '\uF55A' }
}
