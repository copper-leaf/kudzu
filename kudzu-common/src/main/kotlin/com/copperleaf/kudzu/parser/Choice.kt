package com.copperleaf.kudzu.parser

import com.copperleaf.kudzu.Node
import com.copperleaf.kudzu.NodeContext
import com.copperleaf.kudzu.NonTerminalNode
import com.copperleaf.kudzu.Parser
import com.copperleaf.kudzu.ParserContext
import com.copperleaf.kudzu.ParserException

class ChoiceNode(val node: Node, name: String, context: NodeContext) : NonTerminalNode(name, context) {
    override val children: List<Node> get() = listOf(node)
}

/**
 * Given a set of parsers, choose a single one to parse. The first parser that predicts true is chosen. If a parser
 * predicts true, it is expected to parse successfully as well.
 *
 * Predicts true when:
 *   - at least one parser predicts true
 *
 * Parsing fails when:
 *   - none of the provides parsers are able to parse successfully
 */
class ChoiceParser(private vararg val parsers: Parser, name: String = "") : Parser(name) {

    override fun predict(input: ParserContext): Boolean {
        return parsers.any { it.predict(input) }
    }

    override fun parse(input: ParserContext): Pair<Node, ParserContext> {
        for (parser in parsers) {
            if (parser.predict(input)) {
                val next = parser.parse(input)
                return Pair(ChoiceNode(next.first, name, NodeContext(input, next.second)), next.second)
            }
        }

        throw ParserException("No inputs matched", this, input)
    }
}
