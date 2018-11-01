package com.copperleaf.kudzu

actual fun Char._isLetter(): Boolean {
    return "\\w".toRegex().matches("$this")
}

actual fun Char._isDigit(): Boolean {
    return "\\d".toRegex().matches("$this")
}

actual fun Char._isLetterOrDigit(): Boolean {
    return _isLetterOrDigit() || _isDigit()
}