package com.nlkprojects.kwordle.game.words

data class DefinedWord(
    val type: Type,
    val value: String,
    val phonetic: Phonetic? = null,
    val definition: String
) {
    enum class Type {
        Noun, Verb, Adverb, Pronoun, Adjective, Preposition, Conjunction, Interjection
    }
    data class Phonetic(
        val ipa: String?,
        val syllables: String
    )

    companion object {
        fun type(from: String): Type {
            return when {
                from.startsWith("n") -> Type.Noun
                from.startsWith("v") -> Type.Verb
                from.startsWith("adv") -> Type.Adverb
                from.startsWith("pro") -> Type.Pronoun
                from.startsWith("adj") -> Type.Adjective
                from.startsWith("int") -> Type.Interjection
                from.startsWith("pre") -> Type.Preposition
                from.startsWith("con") -> Type.Conjunction

                else -> error("invalid word type provided from: $from")
            }
        }
    }
}
