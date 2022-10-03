package com.copperleaf.kudzu.parser.choice

import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.node.NodeContext
import com.copperleaf.kudzu.node.choice.Choice3Node
import com.copperleaf.kudzu.parser.Parser
import com.copperleaf.kudzu.parser.ParserContext
import com.copperleaf.kudzu.parser.ParserException
import com.copperleaf.kudzu.parser.ParserResult

class Choice3Parser<T1 : Node, T2 : Node, T3 : Node>(
    private val p1: Parser<T1>,
    private val p2: Parser<T2>,
    private val p3: Parser<T3>,
    private val strategy: ChoiceStrategy = ChoiceStrategy.Predictive,
) : Parser<Choice3Node<T1, T2, T3>> {
    override fun predict(input: ParserContext): Boolean {
        return listOf(p1, p2, p3).any { it.predict(input) }
    }

    override val parse = DeepRecursiveFunction<ParserContext, ParserResult<Choice3Node<T1, T2, T3>>> { input ->
        return@DeepRecursiveFunction checkParser(input, p1) { node, context -> Choice3Node.Option1(node, context) }
            ?: checkParser(input, p2) { node, context -> Choice3Node.Option2(node, context) }
            ?: checkParser(input, p3) { node, context -> Choice3Node.Option3(node, context) }
            ?: throw ParserException(
                "No inputs matched",
                this@Choice3Parser,
                input
            )
    }

    private suspend fun <T : Node> DeepRecursiveScope<ParserContext, ParserResult<Choice3Node<T1, T2, T3>>>.checkParser(
        input: ParserContext,
        parser: Parser<T>,
        mapToOptionNode: (T, NodeContext) -> Choice3Node<T1, T2, T3>,
    ): ParserResult<Choice3Node<T1, T2, T3>>? {
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
}
