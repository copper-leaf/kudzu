package com.copperleaf.kudzu.parser.choice

import com.copperleaf.kudzu.node.NodeContext
import com.copperleaf.kudzu.node.choice.ChoiceNNode
import com.copperleaf.kudzu.parser.ParseFunction
import com.copperleaf.kudzu.parser.Parser
import com.copperleaf.kudzu.parser.ParserContext
import com.copperleaf.kudzu.parser.ParserException
import com.copperleaf.kudzu.parser.runParser

/**
 * Given a set of parsers, choose a single one to parse. The first parser that completely succeeds in parsing is chosen.
 *
 * Predicts true when:
 *   - at least one parser predicts true
 *
 * Parsing fails when:
 *   - none of the provided parsers are able to parse successfully
 */
public class ChoiceNParser(
    private val parsers: List<Parser<*>>,
    private val strategy: ChoiceStrategy = ChoiceStrategy.Predictive,
) : Parser<ChoiceNNode> {
    public constructor(
        vararg parsers: Parser<*>,
        strategy: ChoiceStrategy = ChoiceStrategy.Predictive
    ) : this(parsers.toList(), strategy)

    override fun predict(input: ParserContext): Boolean {
        return parsers.any { it.predict(input) }
    }

    override val parse: ParseFunction<ChoiceNNode> = runParser { input ->
        var index = 0
        for (parser in parsers) {
            when (strategy) {
                ChoiceStrategy.Predictive -> {
                    if (parser.predict(input)) {
                        val next = parser.parse.callRecursive(input)
                        return@runParser ChoiceNNode(
                            next.first,
                            index,
                            NodeContext(input, next.second)
                        ) to next.second
                    }
                }
                ChoiceStrategy.Exact -> {
                    val next = kotlin.runCatching { parser.parse.callRecursive(input) }.getOrNull()
                    if (next != null) {
                        return@runParser ChoiceNNode(
                            next.first,
                            index,
                            NodeContext(input, next.second)
                        ) to next.second
                    }
                }
            }

            index++
        }

        throw ParserException(
            "No inputs matched",
            this@ChoiceNParser,
            input
        )
    }
}
