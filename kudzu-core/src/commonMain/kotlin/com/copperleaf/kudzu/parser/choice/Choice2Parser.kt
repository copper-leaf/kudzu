package com.copperleaf.kudzu.parser.choice

import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.node.choice.Choice2Node
import com.copperleaf.kudzu.parser.ParseFunction
import com.copperleaf.kudzu.parser.Parser
import com.copperleaf.kudzu.parser.ParserContext
import com.copperleaf.kudzu.parser.ParserException
import com.copperleaf.kudzu.parser.runParser

public class Choice2Parser<T1 : Node, T2 : Node>(
    private val p1: Parser<T1>,
    private val p2: Parser<T2>,
    private val strategy: ChoiceStrategy = ChoiceStrategy.Predictive,
) : Parser<Choice2Node<T1, T2>> {
    override fun predict(input: ParserContext): Boolean {
        return listOf(p1, p2).any { it.predict(input) }
    }

    override val parse: ParseFunction<Choice2Node<T1, T2>> = runParser { input ->
        return@runParser checkParser(strategy, input, p1) { node, context -> Choice2Node.Option1(node, context) }
            ?: checkParser(strategy, input, p2) { node, context -> Choice2Node.Option2(node, context) }
            ?: throw ParserException(
                "No inputs matched",
                this@Choice2Parser,
                input
            )
    }
}
