package com.copperleaf.kudzu.parser.text

/**
 * Consume zero or more whitespace characters from the input.
 *
 * Predicts true when:
 *   - always
 *
 * Parsing stops when:
 *   - the next character is not whitespace
 *   - there is no more input remaining
 *
 * Parsing fails when:
 *   - never
 */
class OptionalWhitespaceParser : BaseTextParser(
    isValidChar = { _, char -> char.isWhitespace() },
    isValidText = { true },
    allowEmptyInput = true
)
