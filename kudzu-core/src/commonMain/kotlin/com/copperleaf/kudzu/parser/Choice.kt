package com.copperleaf.kudzu.parser

import com.copperleaf.kudzu.Node
import com.copperleaf.kudzu.NodeContext
import com.copperleaf.kudzu.NonTerminalNode
import com.copperleaf.kudzu.Parser
import com.copperleaf.kudzu.ParserContext
import com.copperleaf.kudzu.ParserException
import com.copperleaf.kudzu.test

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
class ChoiceParser(private vararg val parsers: Parser<*>, name: String = "") : Parser<ChoiceNode>(name) {

    override fun predict(input: ParserContext): Boolean {
        return parsers.any { it.predict(input) }
    }

    override fun parse(input: ParserContext): Pair<ChoiceNode, ParserContext> {
        for (parser in parsers) {
            if (parser.predict(input)) {
                val next = parser.parse(input)
                return ChoiceNode(next.first, name, NodeContext(input, next.second)) to next.second
            }
        }

        throw ParserException("No inputs matched", this, input)
    }
}

/**
 * Given a set of parsers, choose a single one to parse. The first parser that predicts true is chosen. If a parser
 * predicts true, it is expected to parse successfully as well.
 *
 * Predicts true when:
 *   - at least one parser parses successfully
 *
 * Parsing fails when:
 *   - none of the provides parsers are able to parse successfully. This should never happen since it predicts true when parsing succeeds
 */
class ExactChoiceParser(private vararg val parsers: Parser<*>, name: String = "") : Parser<ChoiceNode>(name) {

    override fun predict(input: ParserContext): Boolean {
        return parsers.any { it.test(input) != null }
    }

    override fun parse(input: ParserContext): Pair<ChoiceNode, ParserContext> {
        for (parser in parsers) {
            val next = parser.test(input)
            if (next != null) {
                return ChoiceNode(next.first, name, NodeContext(input, next.second)) to next.second
            }
        }

        throw ParserException("No inputs matched", this, input)
    }
}
