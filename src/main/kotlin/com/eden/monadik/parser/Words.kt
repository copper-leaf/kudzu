package com.eden.monadik.parser

import com.eden.monadik.Node
import com.eden.monadik.NodeContext
import com.eden.monadik.Parser
import com.eden.monadik.ParserContext
import com.eden.monadik.ParserException

class WordNode(private val word: String, name: String, context: NodeContext) : Node(name, context) {
    override fun printAst(currentIndent: Int): String {
        return "${indent(currentIndent)}(WordNode$nodeName: $word)"
    }
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
