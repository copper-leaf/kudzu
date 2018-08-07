package com.eden.monadik.parser

import com.eden.monadik.Node
import com.eden.monadik.NodeContext
import com.eden.monadik.Parser
import com.eden.monadik.ParserContext
import com.eden.monadik.ParserException
import com.eden.monadik.TerminalNode

class WordNode(private val word: String, name: String, context: NodeContext) : TerminalNode(name, context) {
    override val text: String get() = word
}

class WordParser(val expected: String, name: String = "") : Parser(name) {
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

class TokenParser(name: String = "") : Parser(name) {
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
