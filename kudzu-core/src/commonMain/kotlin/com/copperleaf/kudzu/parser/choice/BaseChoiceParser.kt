package com.copperleaf.kudzu.parser.choice

import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.node.NodeContext
import com.copperleaf.kudzu.node.choice.ChoiceNode
import com.copperleaf.kudzu.parser.Parser
import com.copperleaf.kudzu.parser.ParserContext
import com.copperleaf.kudzu.parser.ParserException
import com.copperleaf.kudzu.parser.ParserResult

/**
 * Given a set of parsers, choose a single one to parse. The first parser that predicts true is chosen. If a parser
 * predicts true, it is expected to parse successfully as well.
 *
 * This parser is predictive in that it only uses [Parser.predict] to decide which parser to continue with. For some
 * sufficiently ambiguous grammars, this could prove to be significantly more efficient but potentially lead to
 * unexpectedly wrong parse trees. For a less-efficient but more correct "choice" parser, you should use
 * [ExactChoiceParser] instead.
 *
 * Predicts true when:
 *   - at least one parser predicts true
 *
 * Parsing fails when:
 *   - none of the provides parsers are able to parse successfully
 */
@OptIn(ExperimentalStdlibApi::class)
abstract class BaseChoiceParser(
    private vararg val parsers: Parser<*>,
    private val isValidChoice: suspend DeepRecursiveScope<ParserContext, ParserResult<ChoiceNode>>.(
        Parser<*>,
        ParserContext
    ) -> Pair<Boolean, (suspend DeepRecursiveScope<ParserContext, ParserResult<ChoiceNode>>.()->ParserResult<Node>)?>
) : Parser<ChoiceNode>() {

    final override fun predict(input: ParserContext): Boolean {
        return parsers.any { it.predict(input) }
    }

    final override val parse = DeepRecursiveFunction<ParserContext, ParserResult<ChoiceNode>> { input ->
        for (parser in parsers) {
            val (isValid, nextFn) = isValidChoice(parser, input)

            if (isValid && nextFn != null) {
                val next = nextFn()
                ChoiceNode(next.first, NodeContext(input, next.second)) to next.second
            }
        }

        throw ParserException("No inputs matched", this@BaseChoiceParser, input)
    }
}
