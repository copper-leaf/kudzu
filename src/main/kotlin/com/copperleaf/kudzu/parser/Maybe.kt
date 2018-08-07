package com.copperleaf.kudzu.parser

import com.copperleaf.kudzu.Node
import com.copperleaf.kudzu.NodeContext
import com.copperleaf.kudzu.NonTerminalNode
import com.copperleaf.kudzu.Parser
import com.copperleaf.kudzu.ParserContext

class MaybeNode(val node: Node?, name: String, context: NodeContext) : NonTerminalNode(name, context) {
    override val children: List<Node>
        get() = if(node != null) listOf(node) else emptyList()
}

class MaybeParser(val parser: Parser, name: String = "") : Parser(name) {

    override fun predict(input: ParserContext): Boolean {
        return true
    }

    override fun parse(input: ParserContext): Pair<Node, ParserContext> {
        if (parser.predict(input)) {
            val next = parser.parse(input)
            return Pair(MaybeNode(next.first, name, NodeContext(input, next.second)), next.second)
        }
        else {
            return Pair(MaybeNode(null, name, NodeContext(input, input)), input)
        }
    }
}