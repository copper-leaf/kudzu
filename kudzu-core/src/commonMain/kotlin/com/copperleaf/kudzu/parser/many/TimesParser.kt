package com.copperleaf.kudzu.parser.many

import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.parser.Parser

/**
 * Consume input from its parser a specific number of times.
 *
 * Predicts true when:
 *   - there is remaining input
 *   - its parser predicts true
 *
 * Parsing stops when:
 *   - we have parsed `times` number of times already
 *
 * Parsing fails when:
 *   - its parser fails to parse
 *   - there is no more input remaining
 */
class TimesParser<T : Node>(
    parser: Parser<T>,
    private val times: Int,
) : BaseManyParser<T>(
    parser,
    shouldStopParsingAtNodeCount = { it == times },
    shouldStopParsingForNext = { false },
    isValidNodeCount = { it == times },
    invalidNodeCountMessage = { "Expected exactly $times iterations of $parser, got $it" }
)
