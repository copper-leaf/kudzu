package com.copperleaf.kudzu.parser.chars

/**
 * Consumes a single character not in the set of input characters as long as it is not the escapeChar.
 *
 * Predicts true when:
 *   - parsing is successful
 *
 * Parsing fails when:
 *   - the next input character is in the blacklisted characters
 *   - there is no more input
 *   - the escape character is used without input remaining to escape
 */
public class CharNotInParser(
    private val chars: List<Char>,
) : BaseCharParser(
    isValidChar = { it !in chars },
    validationFailedMessage = { "cannot be in [${chars.joinToString()}]" }
) {
    public constructor(vararg chars: Char) : this(chars.toList())
    public constructor(chars: CharRange) : this(chars.toList())
}
