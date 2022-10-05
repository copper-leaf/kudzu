package com.copperleaf.kudzu.parser.choice

import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.node.choice.Choice9Node
import com.copperleaf.kudzu.parser.ParseFunction
import com.copperleaf.kudzu.parser.Parser
import com.copperleaf.kudzu.parser.ParserContext
import com.copperleaf.kudzu.parser.ParserException
import com.copperleaf.kudzu.parser.runParser

/* ktlint-disable max-line-length */
public class Choice9Parser<T1 : Node, T2 : Node, T3 : Node, T4 : Node, T5 : Node, T6 : Node, T7 : Node, T8 : Node, T9 : Node>(
    private val p1: Parser<T1>,
    private val p2: Parser<T2>,
    private val p3: Parser<T3>,
    private val p4: Parser<T4>,
    private val p5: Parser<T5>,
    private val p6: Parser<T6>,
    private val p7: Parser<T7>,
    private val p8: Parser<T8>,
    private val p9: Parser<T9>,
    private val strategy: ChoiceStrategy = ChoiceStrategy.Predictive,
) : Parser<Choice9Node<T1, T2, T3, T4, T5, T6, T7, T8, T9>> {
    override fun predict(input: ParserContext): Boolean {
        return listOf(p1, p2, p3, p4, p5, p6, p7, p8, p9).any { it.predict(input) }
    }

    override val parse: ParseFunction<Choice9Node<T1, T2, T3, T4, T5, T6, T7, T8, T9>> = runParser { input ->
        return@runParser checkParser(strategy, input, p1) { node, context -> Choice9Node.Option1(node, context) }
            ?: checkParser(strategy, input, p2) { node, context -> Choice9Node.Option2(node, context) }
            ?: checkParser(strategy, input, p3) { node, context -> Choice9Node.Option3(node, context) }
            ?: checkParser(strategy, input, p4) { node, context -> Choice9Node.Option4(node, context) }
            ?: checkParser(strategy, input, p5) { node, context -> Choice9Node.Option5(node, context) }
            ?: checkParser(strategy, input, p6) { node, context -> Choice9Node.Option6(node, context) }
            ?: checkParser(strategy, input, p7) { node, context -> Choice9Node.Option7(node, context) }
            ?: checkParser(strategy, input, p8) { node, context -> Choice9Node.Option8(node, context) }
            ?: checkParser(strategy, input, p9) { node, context -> Choice9Node.Option9(node, context) }
            ?: throw ParserException(
                "No inputs matched",
                this@Choice9Parser,
                input
            )
    }
}
/* ktlint-enable max-line-length */
