package com.copperleaf.kudzu

import kotlin.text.isLetter
import kotlin.text.isDigit
import kotlin.text.isLetterOrDigit

/**
 * Acutal/expect declarations for Kudzu
 */
actual object KudzuPlatform {

    actual fun isLetter(char: Char): Boolean {
        return char.isLetter()
    }

    actual fun isDigit(char: Char): Boolean {
        return char.isDigit()
    }

    actual fun isLetterOrDigit(char: Char): Boolean {
        return char.isLetterOrDigit()
    }
}
