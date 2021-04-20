package com.copperleaf.kudzu.parser.chars

/**
 * Consumes a single whitespace character as long as it is not the escapeChar.
 *
 * Predicts true when:
 *   - parsing is successful
 *
 * Parsing fails when:
 *   - the next input character is not whitespace
 *   - there is no more input
 *   - the escape character is used without input remaining to escape
 */
@ExperimentalStdlibApi
class WhitespaceCharParser : BaseCharParser(
    isValidChar = { it.isWhitespace() },
    validationFailedMessage = { "must be whitespace" }
)
