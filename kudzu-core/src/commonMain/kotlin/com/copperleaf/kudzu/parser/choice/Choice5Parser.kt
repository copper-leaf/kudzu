package com.copperleaf.kudzu.parser.choice

import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.node.choice.Choice5Node
import com.copperleaf.kudzu.parser.ParseFunction
import com.copperleaf.kudzu.parser.Parser
import com.copperleaf.kudzu.parser.ParserContext
import com.copperleaf.kudzu.parser.ParserException
import com.copperleaf.kudzu.parser.runParser

/* ktlint-disable max-line-length */
public class Choice5Parser<T1 : Node, T2 : Node, T3 : Node, T4 : Node, T5 : Node>(
    private val p1: Parser<T1>,
    private val p2: Parser<T2>,
    private val p3: Parser<T3>,
    private val p4: Parser<T4>,
    private val p5: Parser<T5>,
    private val strategy: ChoiceStrategy = ChoiceStrategy.Predictive,
) : Parser<Choice5Node<T1, T2, T3, T4, T5>> {
    override fun predict(input: ParserContext): Boolean {
        return listOf(p1, p2, p3, p4, p5).any { it.predict(input) }
    }

    override val parse: ParseFunction<Choice5Node<T1, T2, T3, T4, T5>> = runParser { input ->
        return@runParser checkParser(strategy, input, p1) { node, context -> Choice5Node.Option1(node, context) }
            ?: checkParser(strategy, input, p2) { node, context -> Choice5Node.Option2(node, context) }
            ?: checkParser(strategy, input, p3) { node, context -> Choice5Node.Option3(node, context) }
            ?: checkParser(strategy, input, p4) { node, context -> Choice5Node.Option4(node, context) }
            ?: checkParser(strategy, input, p5) { node, context -> Choice5Node.Option5(node, context) }
            ?: throw ParserException(
                "No inputs matched",
                this@Choice5Parser,
                input
            )
    }
}
/* ktlint-enable max-line-length */
