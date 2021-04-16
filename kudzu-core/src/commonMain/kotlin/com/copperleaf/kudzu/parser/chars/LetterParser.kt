package com.copperleaf.kudzu.parser.chars

import com.copperleaf.kudzu.*

/**
 * Consumes a single letter character as long as it is not the escapeChar.
 *
 * Predicts true when:
 *   - parsing is successful
 *
 * Parsing fails when:
 *   - the next input character is not a letter
 *   - there is no more input
 *   - the escape character is used without input remaining to escape
 */
class LetterParser : BaseCharParser(
    isValidChar = { it.isLetter() },
    validationFailedMessage = { "must be a letter" }
)
