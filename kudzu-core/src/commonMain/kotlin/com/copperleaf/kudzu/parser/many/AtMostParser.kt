package com.copperleaf.kudzu.parser.many

import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.node.NodeContext
import com.copperleaf.kudzu.parser.Parser
import com.copperleaf.kudzu.parser.ParserContext
import com.copperleaf.kudzu.checkNotEmpty
import com.copperleaf.kudzu.node.many.ManyNode

/**
 * Consume input from its parser up to `maxSize` number of times.
 *
 * Predicts true when:
 *   - there is remaining input
 *   - its parser predicts true
 *
 * Parsing stops when:
 *   - its parser predicts false
 *   - there is no more input remaining
 *   - we have parsed `maxSize` number of times already
 *
 * Parsing fails when:
 *   - its parser fails to parse
 */
class AtMostParser<T: Node>(
    parser: Parser<T>,
    private val maxSize: Int,
) : BaseManyParser<T>(
    parser,
    shouldStopParsingAtNodeCount = { it == maxSize },
    shouldStopParsingForNext = { false },
    isValidNodeCount = { it <= maxSize },
)
