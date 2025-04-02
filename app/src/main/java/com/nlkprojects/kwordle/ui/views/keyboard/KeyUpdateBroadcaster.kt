package com.nlkprojects.kwordle.ui.views.keyboard


sealed class KeyUpdate {
    data object Reset: KeyUpdate()
    data class Update(val char: Char, val isValid: Boolean): KeyUpdate()
}

interface KeyUpdateBroadcaster {
    fun register(observer: (KeyUpdate) -> Unit)
}
