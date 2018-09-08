package com.copperleaf.kudzu.parser

import com.copperleaf.kudzu.Node
import com.copperleaf.kudzu.NodeContext
import com.copperleaf.kudzu.Parser
import com.copperleaf.kudzu.ParserContext
import com.copperleaf.kudzu.ParserException
import com.copperleaf.kudzu.TerminalNode

class WordNode(private val word: String, name: String, context: NodeContext) : TerminalNode(name, context) {
    override val text: String get() = word
}

/**
 * Consume a specific sequence of characters of the input.
 *
 * Predicts true when:
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
class WordParser(val expected: String, name: String = "") : Parser(name) {

    override fun predict(input: ParserContext): Boolean {
        return input.next() == expected[0]
    }

    override fun parse(input: ParserContext): Pair<Node, ParserContext> {
        if (input.isEmpty()) throw ParserException("nothing to parse", this, input)
        var remaining = input
        for (i in 0 until expected.length) {
            val nextChar = remaining.next()

            if (nextChar == expected[i]) {
                remaining = remaining.remaining()
            }
            else {
                throw ParserException("expected " + expected, this, input)
            }
        }
        return Pair(WordNode(expected, name, NodeContext(input, remaining)), remaining)
    }
}

/**
 * Consume a sequence of letter or digit characters of the input.
 *
 * Predicts true when:
 *   - the next character is a letter of digit
 *
 * Parsing stops when:
 *   - the next character is not a letter or digit
 *   - there is no more input remaining
 *
 * Parsing fails when:
 *   - no input was consumed
 */
class TokenParser(name: String = "") : Parser(name) {

    override fun predict(input: ParserContext): Boolean {
        return input.next().isLetterOrDigit()
    }

    override fun parse(input: ParserContext): Pair<Node, ParserContext> {
        if (input.isEmpty()) throw ParserException("nothing to parse", this, input)
        var remaining = input
        var nextChar: Char
        var token = ""

        do {
            nextChar = remaining.next()
            if(!nextChar.isLetterOrDigit()) break

            token += nextChar
            remaining = remaining.remaining()
        } while (remaining.isNotEmpty())

        if (token.isEmpty()) throw ParserException("expected a token", this, input)

        return Pair(WordNode(token, name, NodeContext(input, remaining)), remaining)
    }
}
