package com.copperleaf.kudzu.parser.text

import com.copperleaf.kudzu.checkNotEmpty
import com.copperleaf.kudzu.node.NodeContext
import com.copperleaf.kudzu.node.text.TextNode
import com.copperleaf.kudzu.parser.Parser
import com.copperleaf.kudzu.parser.ParserContext
import com.copperleaf.kudzu.parser.ParserException
import com.copperleaf.kudzu.parser.ParserResult
import com.copperleaf.kudzu.parser.choice.PredictiveChoiceParser

/**
 * Consume a specific sequence of characters of the input. Prediction will attempt to consume the entire token, which
 * allows [PredictiveChoiceParser] to correctly match when the choice parsers are all [LiteralTokenParser] but may have
 * ambiguous token matches.
 *
 * Predicts true when:
 *   - there is remaining input
 *   - the next sequence of characters exactly matches [expected]
 *
 * Parsing stops when:
 *   - there is no more input remaining
 *   - the entire expected string has been consumed
 *
 * Parsing fails when:
 *   - there is no more input remaining
 *   - the next character does not match the corresponding character of the expected string
 */
class LiteralTokenParser(
    val expected: String
) : Parser<TextNode> {

    override fun predict(input: ParserContext): Boolean {
        if (input.isEmpty()) return false
        if (doParse(input).first == null) return false

        return true
    }

    override val parse = DeepRecursiveFunction<ParserContext, ParserResult<TextNode>> { input ->
        checkNotEmpty(input) { expected }

        val result = doParse(input)

        val resultFirst = result.first ?: throw ParserException(
            "LiteralTokenParser passed predict(), but failed to parse(). Make sure you're using predict() " +
                "properly, and that the input text has not been modified during parsing. Expected '$expected', " +
                "but got '${result.second}'.",
            this@LiteralTokenParser,
            input
        )

        TextNode(expected, NodeContext(input, resultFirst)) to resultFirst
    }

    private fun isValidChar(index: Int, nextChar: Char): Boolean {
        return index < expected.length && nextChar == expected[index]
    }

    private fun doParse(input: ParserContext): Pair<ParserContext?, String> {
        var remaining = input
        val tokenBuilder = StringBuilder()
        var index = 0

        while (!remaining.isEmpty()) {
            val nextCharResult = remaining.nextChar()
            if (!isValidChar(index, nextCharResult.first)) break

            tokenBuilder.append(nextCharResult.first)
            remaining = nextCharResult.second
            index++
        }

        val token = tokenBuilder.toString()

        return (if (token == expected) remaining else null) to token
    }
}
