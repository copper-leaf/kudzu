package com.copperleaf.kudzu.parser.many

import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.node.NodeContext
import com.copperleaf.kudzu.parser.Parser
import com.copperleaf.kudzu.parser.ParserContext
import com.copperleaf.kudzu.parser.ParserException
import com.copperleaf.kudzu.checkNotEmpty
import com.copperleaf.kudzu.node.many.ManyNode

/**
 * Consume input as many times as its parser is able to, but it must be able to parse a `minSize` number of times.
 *
 * Predicts true when:
 *   - there is remaining input
 *   - its parser predicts true
 *
 * Parsing stops when:
 *   - its parser predicts false
 *   - there is no more input remaining
 *
 * Parsing fails when:
 *   - its parser fails to parse
 *   - its parser could not parse at least `minSize` number of times.
 */
class AtLeastParser<T: Node>(
    parser: Parser<T>,
    private val minSize: Int,
) : BaseManyParser<T>(
    parser,
    shouldStopParsingAtNodeCount = { false },
    shouldStopParsingForNext = { false },
    isValidNodeCount = { it >= minSize },
)
