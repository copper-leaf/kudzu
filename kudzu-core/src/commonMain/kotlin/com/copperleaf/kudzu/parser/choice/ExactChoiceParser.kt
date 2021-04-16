package com.copperleaf.kudzu.parser.choice

import com.copperleaf.kudzu.parser.Parser

/**
 * Given a set of parsers, choose a single one to parse. The first parser that completely succeeds in parsing is chosen.
 *
 * Predicts true when:
 *   - at least one parser predicts true
 *
 * Parsing fails when:
 *   - none of the provided parsers are able to parse successfully
 */
class ExactChoiceParser(
    vararg parsers: Parser<*>
) : BaseChoiceParser(
    *parsers,
    isValidChoice = { parser, input ->
        val test = runCatching { parser.parse(input) }.getOrNull()

        if(test != null) {
            true to { test }
        } else {
            false to null
        }
    }
)
