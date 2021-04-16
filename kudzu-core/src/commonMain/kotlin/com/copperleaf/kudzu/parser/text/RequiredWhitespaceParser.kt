package com.copperleaf.kudzu.parser.text

/**
 * Consume one or more whitespace characters from the input.
 *
 * Predicts true when:
 *   - there is remaining input
 *   - there is input remaining and the next character is whitespace
 *
 * Parsing stops when:
 *   - the next character is not whitespace
 *   - there is no more input remaining
 *
 * Parsing fails when:
 *   - there is no more input remaining
 */
class RequiredWhitespaceParser : BaseTextParser(
    isValidChar = { _, char -> char.isWhitespace() },
    isValidText = { it.isNotEmpty() },
    allowEmptyInput = false
)
