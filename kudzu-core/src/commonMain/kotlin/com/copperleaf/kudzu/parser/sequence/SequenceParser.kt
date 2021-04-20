package com.copperleaf.kudzu.parser.sequence

import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.node.NodeContext
import com.copperleaf.kudzu.node.sequence.SequenceNode
import com.copperleaf.kudzu.parser.Parser
import com.copperleaf.kudzu.parser.ParserContext
import com.copperleaf.kudzu.parser.ParserResult

/**
 * Consume a sequence of parsers in order. Each parser in the sequence must parse successfully.
 *
 * Predicts true when:
 *   - the first parser predicts true
 *
 * Parsing fails when:
 *   - any of its parsers fails to parse
 */
@ExperimentalStdlibApi
class SequenceParser(
    private vararg val parsers: Parser<*>,
) : Parser<SequenceNode>() {

    override fun predict(input: ParserContext): Boolean {
        return parsers.first().predict(input)
    }

    override val parse = DeepRecursiveFunction<ParserContext, ParserResult<SequenceNode>> { input ->
        val nodeList = ArrayList<Node>()

        var remaining = input
        var next: ParserResult<Node>?
        for (parser in parsers) {
            next = parser.parse.callRecursive(remaining)
            nodeList.add(next.first)
            remaining = next.second
        }

        SequenceNode(nodeList, NodeContext(input, remaining)) to remaining
    }
}
