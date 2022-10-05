package com.copperleaf.kudzu.parser.maybe

import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.node.NodeContext
import com.copperleaf.kudzu.node.maybe.MaybeNode
import com.copperleaf.kudzu.parser.ParseFunction
import com.copperleaf.kudzu.parser.Parser
import com.copperleaf.kudzu.parser.ParserContext
import com.copperleaf.kudzu.parser.runParser

/**
 * Maybe consume input from its parser. If its parser predicts true, it is expected to parse successfully.
 *
 * Predicts true when:
 *   - always
 *
 * Parsing fails when:
 *   - its parser fails to parse
 */
public class MaybeParser<T : Node>(
    public val parser: Parser<T>,
) : Parser<MaybeNode<T>> {

    override fun predict(input: ParserContext): Boolean {
        return true
    }

    override val parse: ParseFunction<MaybeNode<T>> = runParser { input ->
        if (parser.predict(input)) {
            val next = parser.parse.callRecursive(input)
            MaybeNode(next.first, NodeContext(input, next.second)) to next.second
        } else {
            MaybeNode<T>(null, NodeContext(input, input)) to input
        }
    }
}
