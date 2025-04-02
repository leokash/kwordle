package com.nlkprojects.kwordle.game.words

import kotlinx.serialization.Serializable

@Serializable
data class WordDto(
    val ipa: String = "",
    val entry: String,
    val meaning: Map<String, String>
)
