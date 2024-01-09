package com.copperleaf.kudzu.parser.choice

import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.node.NodeContext
import com.copperleaf.kudzu.parser.ParseScope
import com.copperleaf.kudzu.parser.Parser
import com.copperleaf.kudzu.parser.ParserContext
import com.copperleaf.kudzu.parser.ParserResult

public enum class ChoiceStrategy {
    Predictive,
    Exact
}

internal suspend fun <InputT : Node, OutputT : Node> ParseScope<OutputT>.checkParser(
    strategy: ChoiceStrategy,
    input: ParserContext,
    parser: Parser<InputT>,
    mapToOptionNode: (InputT, NodeContext) -> OutputT,
): ParserResult<OutputT>? {
    return when (strategy) {
        ChoiceStrategy.Predictive -> {
            if (parser.predict(input)) {
                val parsedNode = parser.parse.callRecursive(input)
                mapToOptionNode(parsedNode.first, NodeContext(input, parsedNode.second)) to parsedNode.second
            } else {
                null
            }
        }

        ChoiceStrategy.Exact -> {
            val parsedNode = kotlin.runCatching { parser.parse.callRecursive(input) }.getOrNull()

            if (parsedNode != null) {
                mapToOptionNode(parsedNode.first, NodeContext(input, parsedNode.second)) to parsedNode.second
            } else {
                null
            }
        }
    }
}
