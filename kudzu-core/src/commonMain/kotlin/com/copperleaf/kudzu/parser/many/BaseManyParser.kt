package com.copperleaf.kudzu.parser.many

import com.copperleaf.kudzu.checkNotEmpty
import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.node.NodeContext
import com.copperleaf.kudzu.parser.Parser
import com.copperleaf.kudzu.node.many.ManyNode
import com.copperleaf.kudzu.parser.ParserContext
import com.copperleaf.kudzu.parser.ParserException
import com.copperleaf.kudzu.parser.ParserResult

abstract class BaseManyParser<T: Node>(
    private val parser: Parser<T>,
    private val shouldStopParsingAtNodeCount: (Int) -> Boolean,
    private val shouldStopParsingForNext: (ParserContext) -> Boolean,
    private val isValidNodeCount: (Int) -> Boolean,
) : Parser<ManyNode<T>>() {
    override fun predict(input: ParserContext): Boolean {
        return input.isNotEmpty() && parser.predict(input)
    }

    override fun parse(input: ParserContext): ParserResult<ManyNode<T>> {
        checkNotEmpty(input)

        val nodeList = ArrayList<T>()

        var remaining = input
        var next: ParserResult<T>?
        while (remaining.isNotEmpty()) {
            if (shouldStopParsingAtNodeCount(nodeList.size)) break

            if (parser.predict(remaining)) {
                if (shouldStopParsingForNext(remaining)) break

                next = parser.parse(remaining)
                nodeList.add(next.first)
                remaining = next.second
            } else {
                break
            }
        }

        if (!isValidNodeCount(nodeList.size)) throw ParserException(
            "",
            this,
            input
        )

        return ManyNode(nodeList, NodeContext(input, remaining)) to remaining
    }
}
