package com.copperleaf.kudzu

/**
 * Acutal/expect declarations for Kudzu
 */
expect object KudzuPlatform {

    fun isLetter(char: Char): Boolean

    fun isDigit(char: Char): Boolean

    fun isLetterOrDigit(char: Char): Boolean
}
