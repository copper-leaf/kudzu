package com.copperleaf.kudzu.parser.many

import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.node.NodeContext
import com.copperleaf.kudzu.parser.Parser
import com.copperleaf.kudzu.parser.ParserContext
import com.copperleaf.kudzu.checkNotEmpty
import com.copperleaf.kudzu.node.many.ManyNode

/**
 * Consume input as many times as its parser is able to.
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
 */
class ManyParser<T: Node>(
    parser: Parser<T>,
) : BaseManyParser<T>(
    parser,
    shouldStopParsingAtNodeCount = { false },
    shouldStopParsingForNext = { false },
    isValidNodeCount = { true },
)
