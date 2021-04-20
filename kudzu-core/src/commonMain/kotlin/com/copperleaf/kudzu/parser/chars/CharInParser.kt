package com.copperleaf.kudzu.parser.chars

/**
 * Consumes a single character from the set of input characters as long as it is not the escapeChar.
 *
 * Predicts true when:
 *   - parsing is successful
 *
 * Parsing fails when:
 *   - the next input character is not in the whitelisted characters
 *   - there is no more input
 *   - the escape character is used without input remaining to escape
 */
@ExperimentalStdlibApi
class CharInParser(
    private vararg val chars: Char,
) : BaseCharParser(
    isValidChar = { chars.contains(it) },
    validationFailedMessage = { "not in [${chars.joinToString()}]" }
)
