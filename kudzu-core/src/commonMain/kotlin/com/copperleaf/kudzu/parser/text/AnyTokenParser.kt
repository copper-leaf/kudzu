package com.copperleaf.kudzu.parser.text

import com.copperleaf.kudzu.KudzuPlatform

/**
 * Consume a sequence of letter or digit characters of the input.
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
@ExperimentalStdlibApi
class AnyTokenParser : BaseTextParser(
    isValidChar = { _, char -> KudzuPlatform.isLetterOrDigit(char) },
    isValidText = { it.isNotEmpty() },
    allowEmptyInput = false
)
