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
class CharInParser(
    private val chars: List<Char>,
) : BaseCharParser(
    isValidChar = { it in chars },
    validationFailedMessage = { "not in [${chars.joinToString()}]" }
) {
    constructor(vararg chars: Char) : this(chars.toList())
    constructor(chars: CharRange) : this(chars.toList())
}
