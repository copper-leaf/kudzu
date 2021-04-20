package com.copperleaf.kudzu.parser.text

/**
 * Consume a specific sequence of characters of the input.
 *
 * Predicts true when:
 *   - there is remaining input
 *   - the next character matches the first expected character
 *
 * Parsing stops when:
 *   - the next character is not whitespace
 *   - there is no more input remaining
 *   - the entire expected string has been consumed
 *
 * Parsing fails when:
 *   - there is no more input remaining
 *   - the next character does not match the corresponding character of the expected string
 */
@ExperimentalStdlibApi
class WordParser(
    val expected: String
) : BaseTextParser(
    isValidChar = { index, char -> index < expected.length && char == expected[index] },
    isValidText = { it == expected },
    allowEmptyInput = false
)
