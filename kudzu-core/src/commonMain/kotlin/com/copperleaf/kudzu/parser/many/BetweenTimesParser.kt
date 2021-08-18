package com.copperleaf.kudzu.parser.many

import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.parser.Parser

/**
 * Consume input from its parser between `minSize` and `maxSize` number of times.
 *
 * Predicts true when:
 *   - there is remaining input
 *   - its parser predicts true
 *
 * Parsing stops when:
 *   - we have parsed `maxSize` number of times already
 *
 * Parsing fails when:
 *   - its parser fails to parse
 *   - its parser could not parse at least `minSize` number of times.
 */
@ExperimentalStdlibApi
class BetweenTimesParser<T : Node>(
    parser: Parser<T>,
    private val minSize: Int,
    private val maxSize: Int,
) : BaseManyParser<T>(
    parser,
    shouldStopParsingAtNodeCount = { it == maxSize },
    shouldStopParsingForNext = { false },
    isValidNodeCount = { it in minSize..maxSize },
    invalidNodeCountMessage = { "Expected between $minSize and $maxSize iterations of $parser, got $it" }
)
