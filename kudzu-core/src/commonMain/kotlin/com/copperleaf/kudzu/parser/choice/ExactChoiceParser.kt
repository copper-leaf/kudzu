package com.copperleaf.kudzu.parser.choice

import com.copperleaf.kudzu.node.NodeContext
import com.copperleaf.kudzu.node.choice.ChoiceNode
import com.copperleaf.kudzu.parser.Parser
import com.copperleaf.kudzu.parser.ParserContext
import com.copperleaf.kudzu.parser.ParserException
import com.copperleaf.kudzu.parser.ParserResult

/**
 * Given a set of parsers, choose a single one to parse. The first parser that completely succeeds in parsing is chosen.
 *
 * Predicts true when:
 *   - at least one parser predicts true
 *
 * Parsing fails when:
 *   - none of the provided parsers are able to parse successfully
 */
@ExperimentalStdlibApi
class ExactChoiceParser(
    private vararg val parsers: Parser<*>
) : Parser<ChoiceNode>() {

    override fun predict(input: ParserContext): Boolean {
        return parsers.any { it.predict(input) }
    }

    override val parse = DeepRecursiveFunction<ParserContext, ParserResult<ChoiceNode>> { input ->
        for (parser in parsers) {
            val next = kotlin.runCatching { parser.parse.callRecursive(input) }.getOrNull()

            if (next != null) {
                return@DeepRecursiveFunction ChoiceNode(next.first, NodeContext(input, next.second)) to next.second
            }
        }

        throw ParserException(
            "No inputs matched",
            this@ExactChoiceParser,
            input
        )
    }
}
