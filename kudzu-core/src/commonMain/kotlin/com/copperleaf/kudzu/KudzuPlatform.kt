@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package com.copperleaf.kudzu

/**
 * Acutal/expect declarations for Kudzu
 */
public expect object KudzuPlatform {
    public fun isLetter(char: Char): Boolean

    public fun isDigit(char: Char): Boolean

    public fun isLetterOrDigit(char: Char): Boolean
}
