package com.copperleaf.kudzu.parser

import com.copperleaf.kudzu.Node
import com.copperleaf.kudzu.NodeContext
import com.copperleaf.kudzu.NonTerminalNode
import com.copperleaf.kudzu.Parser
import com.copperleaf.kudzu.ParserContext

class SequenceNode(val nodeList: List<Node>, name: String, context: NodeContext) : NonTerminalNode(name, context) {
    override val children: List<Node> get() = nodeList
}

class SequenceParser(private vararg val parsers: Parser, name: String = "") : Parser(name) {

    override fun predict(input: ParserContext): Boolean {
        return parsers.first().predict(input)
    }

    override fun parse(input: ParserContext): Pair<Node, ParserContext> {
        val nodeList = ArrayList<Node>()

        var remaining = input
        var next: Pair<Node, ParserContext>?
        for (parser in parsers) {
            next = parser.parse(remaining)
            nodeList.add(next.first)
            remaining = next.second
        }

        return Pair(SequenceNode(nodeList, name, NodeContext(input, remaining)), remaining)
    }
}
