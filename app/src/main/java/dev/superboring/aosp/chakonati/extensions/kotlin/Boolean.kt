package dev.superboring.aosp.chakonati.extensions.kotlin

typealias ElseBoolean = Boolean

inline infix fun Boolean.then(doThis: () -> Unit): ElseBoolean {
    if (this) {
        doThis()
    }
    return !this
}

inline infix fun Boolean.notThen(doThis: () -> Unit) {
    if (!this) {
        doThis()
    }
}

inline infix fun Boolean.elseThen(doThis: () -> Unit) {
    this then doThis
}
