package com.copperleaf.kudzu.parser

import com.copperleaf.kudzu.Node
import com.copperleaf.kudzu.NonTerminalNode
import com.copperleaf.kudzu.Parser
import com.copperleaf.kudzu.ParserContext

class NamedNode<T : Node>(
    private val node: T,
    name: String
) : NonTerminalNode(name, node.context) {
    override val children: List<Node> get() = listOf(node)
}

class NamedParser<T : Node>(val parser: Parser<T>, name: String) : Parser<NamedNode<T>>(name) {

    override fun predict(input: ParserContext): Boolean = parser.predict(input)

    override fun parse(input: ParserContext): Pair<NamedNode<T>, ParserContext> {
        val node = parser.parse(input)
        return NamedNode(node.first, this.name) to node.second
    }
}
