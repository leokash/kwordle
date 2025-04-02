package com.nlkprojects.kwordle

import android.content.Context

data class Prefs(private val context: Context) {
    private val prefsKey = "com.nlkprojects.kwordle.sharedPref"
    private val prefsStore = context.getSharedPreferences(prefsKey, Context.MODE_PRIVATE)

    fun getString(key: String): String? {
        return prefsStore.getString(key, null)
    }
    fun putString(key: String, data: String?) {
        with(prefsStore.edit()) {
            putString(key, data)
            commit()
        }
    }
    fun getStrings(key: String): Set<String>? {
        return prefsStore.getStringSet(key, null)
    }
    fun putStrings(key: String, data: Set<String>) {
        with(prefsStore.edit()) {
            putStringSet(key, data)
            commit()
        }
    }
}
