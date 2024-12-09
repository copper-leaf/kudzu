@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package com.copperleaf.kudzu

/**
 * Acutal/expect declarations for Kudzu
 */
public actual object KudzuPlatform {
    public actual fun isLetter(char: Char): Boolean {
        return char.isLetter()
    }

    public actual fun isDigit(char: Char): Boolean {
        return char.isDigit()
    }

    public actual fun isLetterOrDigit(char: Char): Boolean {
        return char.isLetterOrDigit()
    }
}
