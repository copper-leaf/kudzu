package com.copperleaf.kudzu.parser.chars

/**
 * Consumes a single character as long as it is not the escapeChar.
 *
 * Predicts true when:
 *   - parsing is successful
 *
 * Parsing fails when:
 *   - there is no more input
 *   - the escape character is used without input remaining to escape
 */
public class AnyCharParser : BaseCharParser(
    isValidChar = { true },
    validationFailedMessage = { "" }
)
