package dev.superboring.aosp.chakonati.extensions.kotlin

inline infix fun Boolean.then(doThis: () -> Unit) {
    if (this) {
        doThis()
    }
}

inline infix fun Boolean.notThen(doThis: () -> Unit) {
    if (!this) {
        doThis()
    }
}