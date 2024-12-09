package com.copperleaf.kudzu

/**
 * Acutal/expect declarations for Kudzu
 */
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
public actual object KudzuPlatform {
    public actual fun isLetter(char: Char): Boolean {
        return char in (('a'..'z') + ('A'..'Z'))
    }

    public actual fun isDigit(char: Char): Boolean {
        return char in ('0'..'9')
    }

    public actual fun isLetterOrDigit(char: Char): Boolean {
        return isLetter(char) || isDigit(char)
    }
}
