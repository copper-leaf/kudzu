package com.eden.monadik.parser

import com.eden.monadik.Node
import com.eden.monadik.NodeContext
import com.eden.monadik.Parser
import com.eden.monadik.ParserContext
import com.eden.monadik.ParserException
import com.eden.monadik.TerminalNode

class WhitespaceNode(name: String, context: NodeContext) : TerminalNode(name, context) {
    override val text: String get() = ""
}

class OptionalWhitespaceParser(name: String = "") : Parser(name) {
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

class RequiredWhitespaceParser(name: String = "") : Parser(name) {
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
