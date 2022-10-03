package com.copperleaf.kudzu.parser.chars

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
class HexDigitParser : BaseCharParser(
    isValidChar = { it in (('0'..'9') + ('a'..'f') + ('A'..'F')) },
    validationFailedMessage = { "must be a letter or digit" }
)
