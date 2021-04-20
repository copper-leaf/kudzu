package com.copperleaf.kudzu.parser.chars

import com.copperleaf.kudzu.isDigit

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
@ExperimentalStdlibApi
class DigitParser : BaseCharParser(
    isValidChar = { it.isDigit() },
    validationFailedMessage = { "must be a digit" }
)
