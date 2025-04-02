package com.nlkprojects.kwordle.game

data class Stat(
    val won: Boolean,
    val length: Int,
    val attempts: Int,
    val duration: Long
) {
    override fun toString(): String {
        return "$won|$length|$attempts|$duration"
    }
    companion object {
        fun stats(from: Set<String>): List<Stat> {
            return buildList {
                from.forEach { line ->
                    val parts = line.split("|")
                    add(Stat(parts[0].toBoolean(), parts[1].toInt(), parts[2].toInt(), parts[3].toLong()))
                }
            }
        }
    }
}
