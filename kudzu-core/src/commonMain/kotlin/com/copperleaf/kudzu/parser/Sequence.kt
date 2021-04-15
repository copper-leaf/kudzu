package com.copperleaf.kudzu.parser

import com.copperleaf.kudzu.Node
import com.copperleaf.kudzu.NodeContext
import com.copperleaf.kudzu.NonTerminalNode
import com.copperleaf.kudzu.Parser
import com.copperleaf.kudzu.ParserContext

class SequenceNode(val nodeList: List<Node>, name: String, context: NodeContext) : NonTerminalNode(name, context) {
    override val children: List<Node> get() = nodeList
}

/**
 * Consume a sequence of parsers in order. Each parser in the sequence must parse successfully.
 *
 * Predicts true when:
 *   - the first parser predicts true
 *
 * Parsing fails when:
 *   - any of its parsers fails to parse
 */
class SequenceParser(private vararg val parsers: Parser<*>, name: String = "") : Parser<SequenceNode>(name) {

    override fun predict(input: ParserContext): Boolean {
        return parsers.first().predict(input)
    }

    override fun parse(input: ParserContext): Pair<SequenceNode, ParserContext> {
        val nodeList = ArrayList<Node>()

        var remaining = input
        var next: Pair<Node, ParserContext>?
        for (parser in parsers) {
            next = parser.parse(remaining)
            nodeList.add(next.first)
            remaining = next.second
        }

        return SequenceNode(nodeList, name, NodeContext(input, remaining)) to remaining
    }
}
