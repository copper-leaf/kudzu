package com.copperleaf.kudzu.parser.maybe

import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.node.NodeContext
import com.copperleaf.kudzu.parser.Parser
import com.copperleaf.kudzu.node.maybe.MaybeNode
import com.copperleaf.kudzu.parser.ParserContext
import com.copperleaf.kudzu.parser.ParserResult

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
) : Parser<MaybeNode<T>>() {

    override fun predict(input: ParserContext): Boolean {
        return true
    }

    override fun parse(input: ParserContext): ParserResult<MaybeNode<T>> {
        if (parser.predict(input)) {
            val next = parser.parse(input)
            return MaybeNode(next.first, NodeContext(input, next.second)) to next.second
        } else {
            return MaybeNode<T>(null, NodeContext(input, input)) to input
        }
    }
}
