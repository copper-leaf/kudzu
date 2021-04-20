package com.copperleaf.kudzu.parser.chars

import com.copperleaf.kudzu.isLetterOrDigit

/**
 * Consumes a single letter character as long as it is not the escapeChar.
 *
 * Predicts true when:
 *   - parsing is successful
 *
 * Parsing fails when:
 *   - the next input character is neither a letter nor a digit
 *   - there is no more input
 *   - the escape character is used without input remaining to escape
 */
@ExperimentalStdlibApi
class LetterOrDigitParser : BaseCharParser(
    isValidChar = { it.isLetterOrDigit() },
    validationFailedMessage = { "must be a letter or digit" }
)
