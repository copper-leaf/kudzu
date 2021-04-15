package com.copperleaf.kudzu.parser

import com.copperleaf.kudzu.Node
import com.copperleaf.kudzu.NodeContext
import com.copperleaf.kudzu.NonTerminalNode
import com.copperleaf.kudzu.Parser
import com.copperleaf.kudzu.ParserContext

class MaybeNode<T: Node>(
    val node: T?,
    name: String,
    context: NodeContext
) : NonTerminalNode(name, context) {
    override val children: List<Node>
        get() = if (node != null) listOf(node) else emptyList()
}

/**
 * Maybe consume input from its parser. If its parser predicts true, it is expected to parse successfully.
 *
 * Predicts true when:
 *   - always
 *
 * Parsing fails when:
 *   - its parser fails to parse
 */
class MaybeParser<T: Node>(
    val parser: Parser<T>,
    name: String = ""
) : Parser<MaybeNode<T>>(name) {

    override fun predict(input: ParserContext): Boolean {
        return true
    }

    override fun parse(input: ParserContext): Pair<MaybeNode<T>, ParserContext> {
        if (parser.predict(input)) {
            val next = parser.parse(input)
            return MaybeNode(next.first, name, NodeContext(input, next.second)) to next.second
        } else {
            return MaybeNode<T>(null, name, NodeContext(input, input)) to input
        }
    }
}
