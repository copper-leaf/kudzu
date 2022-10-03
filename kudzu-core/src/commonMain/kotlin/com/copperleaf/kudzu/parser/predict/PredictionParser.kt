package com.copperleaf.kudzu.parser.predict

import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.parser.Parser
import com.copperleaf.kudzu.parser.ParserContext
import com.copperleaf.kudzu.parser.ParserException
import com.copperleaf.kudzu.parser.ParserResult

/**
 * A Parser that runs another parser during _prediction_. If the [parser.parse] succeeds in parsing, then the [predict]
 * of this parser will return true. In is useful in cases where you need to check that a small sequence of parsers
 * succeeds as a prediction, instead of only a single parser.
 *
 * Be careful using this parser. Because it runs a full parser during the [predict] step, it will inevitably run that
 * parser **twice** during the entire parsing run: once during [predict], and again during [parse]. Not only that, but
 * recursion within this [parser] could easily end up giving you an incredibly inefficient parse. As such,
 * [PredictionParser] should only be used with terminal parsers or simple sequences around terminal parsers, in such a
 * manner that it really only ever needs to consume a few characters or one or two tokens to determine if prediction
 * should succeed.
 *
 * Predicts true when:
 *   - [parser] predicts true
 *   - [parser] parses successfully without throwing [ParserException]
 *
 * Parsing fails when:
 *   - [parser] fails to parse
 */
class PredictionParser<T : Node>(
    val parser: Parser<T>
) : Parser<T> {

    override fun predict(input: ParserContext): Boolean {
        if (!parser.predict(input)) return false

        return try {
            parser.parse(input)
            true
        } catch (e: ParserException) {
            false
        }
    }

    override val parse: DeepRecursiveFunction<ParserContext, ParserResult<T>> = parser.parse
}
