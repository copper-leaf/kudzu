package com.copperleaf.kudzu.parser.many

import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.node.NodeContext
import com.copperleaf.kudzu.node.many.ManyNode
import com.copperleaf.kudzu.parser.Parser
import com.copperleaf.kudzu.parser.ParserContext
import com.copperleaf.kudzu.parser.ParserException
import com.copperleaf.kudzu.parser.ParserResult

abstract class BaseManyParser<T : Node>(
    private val parser: Parser<T>,
    private val shouldStopParsingAtNodeCount: (Int) -> Boolean,
    private val shouldStopParsingForNext: (ParserContext) -> Boolean,
    private val isValidNodeCount: (Int) -> Boolean,
    private val invalidNodeCountMessage: (Int) -> String,
) : Parser<ManyNode<T>> {
    final override fun predict(input: ParserContext): Boolean {
        return parser.predict(input)
    }

    final override val parse = DeepRecursiveFunction<ParserContext, ParserResult<ManyNode<T>>> { input ->
        val nodeList = mutableListOf<T>()

        var remaining = input
        var next: ParserResult<T>?
        while (!remaining.isEmpty()) {
            if (shouldStopParsingAtNodeCount(nodeList.size)) break

            if (parser.predict(remaining)) {
                if (shouldStopParsingForNext(remaining)) break

                next = parser.parse.callRecursive(remaining)
                nodeList.add(next.first)
                remaining = next.second
            } else {
                break
            }
        }

        if (!isValidNodeCount(nodeList.size)) throw ParserException(
            invalidNodeCountMessage(nodeList.size),
            this@BaseManyParser,
            input
        )

        ManyNode(nodeList, NodeContext(input, remaining)) to remaining
    }
}
