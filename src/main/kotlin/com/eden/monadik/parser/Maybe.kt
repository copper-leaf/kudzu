package com.eden.monadik.parser

import com.eden.monadik.Node
import com.eden.monadik.NodeContext
import com.eden.monadik.NonTerminalNode
import com.eden.monadik.Parser
import com.eden.monadik.ParserContext

class MaybeNode(val node: Node?, name: String, context: NodeContext) : NonTerminalNode(name, context) {
    override val children: List<Node>
        get() = if(node != null) listOf(node) else emptyList()
}

class MaybeParser(val parser: Parser, name: String = "") : Parser(name) {
    override fun parse(input: ParserContext): Pair<Node, ParserContext> {
        val next = parser.test(input)
        if (next != null) {
            return Pair(MaybeNode(next.first, name, NodeContext(input, next.second)), next.second)
        }
        else {
            return Pair(MaybeNode(null, name, NodeContext(input, input)), input)
        }
    }
}