package com.copperleaf.kudzu

/**
 * Acutal/expect declarations for Kudzu
 */
public expect object KudzuPlatform {
    public fun isLetter(char: Char): Boolean

    public fun isDigit(char: Char): Boolean

    public fun isLetterOrDigit(char: Char): Boolean
}
