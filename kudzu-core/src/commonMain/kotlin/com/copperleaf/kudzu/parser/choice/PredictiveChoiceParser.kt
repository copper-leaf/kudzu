package com.copperleaf.kudzu.parser.choice

import com.copperleaf.kudzu.parser.Parser

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
class PredictiveChoiceParser(
    private vararg val parsers: Parser<*>
) : BaseChoiceParser(
    *parsers,
    isValidChoice = { parser, input ->
        parser.predict(input) to { parser.parse(input)}
    }
)

