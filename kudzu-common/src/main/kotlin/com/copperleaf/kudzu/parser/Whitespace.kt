package com.copperleaf.kudzu.parser

import com.copperleaf.kudzu.Node
import com.copperleaf.kudzu.NodeContext
import com.copperleaf.kudzu.Parser
import com.copperleaf.kudzu.ParserContext
import com.copperleaf.kudzu.ParserException
import com.copperleaf.kudzu.TerminalNode

class WhitespaceNode(name: String, context: NodeContext) : TerminalNode(name, context) {
    override val text: String get() = ""
}

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
class OptionalWhitespaceParser(name: String = "") : Parser(name) {

    override fun predict(input: ParserContext): Boolean {
        return true
    }

    override fun parse(input: ParserContext): Pair<Node, ParserContext> {
        var remaining = input
        var nextChar: Char
        var token = ""

        while (remaining.isNotEmpty()) {
            nextChar = remaining.next()
            if (!nextChar.isWhitespace()) break

            token += nextChar
            remaining = remaining.remaining()
        }

        return Pair(WhitespaceNode(name, NodeContext(input, remaining)), remaining)
    }
}

/**
 * Consume one or more whitespace characters from the input.
 *
 * Predicts true when:
 *   - there is input remaining and the next character is whitespace
 *
 * Parsing stops when:
 *   - the next character is not whitespace
 *   - there is no more input remaining
 *
 * Parsing fails when:
 *   - there is no more input remaining
 */
class RequiredWhitespaceParser(name: String = "") : Parser(name) {

    override fun predict(input: ParserContext): Boolean {
        return input.isNotEmpty() && input.next().isWhitespace()
    }

    override fun parse(input: ParserContext): Pair<Node, ParserContext> {
        if (input.isEmpty()) throw ParserException("nothing to parse", this, input)
        var remaining = input
        var nextChar: Char
        var token = ""

        do {
            nextChar = remaining.next()
            if(!nextChar.isWhitespace()) break

            token += nextChar
            remaining = remaining.remaining()
        } while (remaining.isNotEmpty())

        return Pair(WhitespaceNode(name, NodeContext(input, remaining)), remaining)
    }
}
