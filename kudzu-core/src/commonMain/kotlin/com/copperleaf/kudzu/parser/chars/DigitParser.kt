package com.copperleaf.kudzu.parser.chars

import com.copperleaf.kudzu.KudzuPlatform

/**
 * Consumes a single digit character as long as it is not the escapeChar.
 *
 * Predicts true when:
 *   - parsing is successful
 *
 * Parsing fails when:
 *   - the next input character is not a digit
 *   - there is no more input
 *   - the escape character is used without input remaining to escape
 */
class DigitParser : BaseCharParser(
    isValidChar = { KudzuPlatform.isDigit(it) },
    validationFailedMessage = { "must be a digit" }
)
