package com.copperleaf.kudzu.parser.text

import com.copperleaf.kudzu.checkNotEmpty
import com.copperleaf.kudzu.node.NodeContext
import com.copperleaf.kudzu.node.text.TextNode
import com.copperleaf.kudzu.parser.Parser
import com.copperleaf.kudzu.parser.ParserContext
import com.copperleaf.kudzu.parser.ParserException
import com.copperleaf.kudzu.parser.ParserResult

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
abstract class BaseTextParser(
    val isValidChar: (Int, Char) -> Boolean,
    val isValidText: (String) -> Boolean,
    val allowEmptyInput: Boolean
) : Parser<TextNode>() {

    final override fun predict(input: ParserContext): Boolean {
        return if (input.isEmpty()) {
            allowEmptyInput
        } else {
            isValidChar(0, input.next())
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    override val parse = DeepRecursiveFunction<ParserContext, ParserResult<TextNode>> { input ->
        if (!allowEmptyInput) {
            checkNotEmpty(input)
        }

        var remaining = input
        var nextChar: Char
        var token = ""
        var index = 0

        while (remaining.isNotEmpty()) {
            nextChar = remaining.next()
            if (!isValidChar(index, nextChar)) break

            token += nextChar
            remaining = remaining.remaining()
            index++
        }

        if (!isValidText(token)) throw ParserException(
            "Unexpected text '$token' in ${this::class.simpleName}",
            this@BaseTextParser,
            input
        )

        TextNode(token, NodeContext(input, remaining)) to remaining
    }
}
