
package com.nlkprojects.kwordle.utils

fun <T> Result<T>.onSuccess(f: (T) -> Unit): Result<T> {
    getOrNull()?.also(f)
    return this
}
