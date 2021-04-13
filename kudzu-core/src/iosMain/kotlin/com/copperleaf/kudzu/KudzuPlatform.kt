package com.copperleaf.kudzu

/**
 * Acutal/expect declarations for Kudzu
 */
actual object KudzuPlatform {

    actual fun isLetter(char: Char): Boolean {
        return char in (('a'..'z') + ('A'..'Z'))
    }

    actual fun isDigit(char: Char): Boolean {
        return char in ('0'..'9')
    }

    actual fun isLetterOrDigit(char: Char): Boolean {
        return isLetter(char) || isDigit(char)
    }
}
