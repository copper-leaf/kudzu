package com.copperleaf.kudzu.parser.text

/**
 * Consume a sequence of any non-whitespace characters from the input
 *
 * Predicts true when:
 *   - there is remaining input
 *   - the next character is a letter of digit
 *
 * Parsing stops when:
 *   - the next character is not a letter or digit
 *   - there is no more input remaining
 *
 * Parsing fails when:
 *   - no input was consumed
 */
class AnyTextParser : BaseTextParser(
    isValidChar = { _, char -> !char.isWhitespace() },
    isValidText = { it.isNotEmpty() },
    allowEmptyInput = false,
    invalidTextErrorMessage = { "Expected non-whitespace text, got '$it'" },
)
