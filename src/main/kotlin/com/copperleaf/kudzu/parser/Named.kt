package com.copperleaf.kudzu.parser

import com.copperleaf.kudzu.Node
import com.copperleaf.kudzu.NonTerminalNode
import com.copperleaf.kudzu.Parser
import com.copperleaf.kudzu.ParserContext

class NamedNode(private val node: Node, name: String) : NonTerminalNode(name, node.context) {
    override val children: List<Node> get() = listOf(node)
}

class NamedParser(val parser: Parser, name: String) : Parser(name) {

    override fun predict(input: ParserContext): Boolean = parser.predict(input)

    override fun parse(input: ParserContext): Pair<Node, ParserContext> {
        val node = parser.parse(input)
        return Pair(NamedNode(node.first, this.name), node.second)
    }

}
