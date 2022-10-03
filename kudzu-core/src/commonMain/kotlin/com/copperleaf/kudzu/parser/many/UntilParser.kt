package com.copperleaf.kudzu.parser.many

import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.parser.Parser

/**
 * Consume input as many times as its parser is able to, or until a stopping condition is reached.
 *
 * Predicts true when:
 *   - there is remaining input
 *   - its parser predicts true
 *
 * Parsing stops when:
 *   - its parser predicts false
 *   - the stopping condition predicts true
 *   - there is no more input remaining
 *
 * Parsing fails when:
 *   - its parser fails to parse
 */
class UntilParser<T : Node>(
    parser: Parser<T>,
    private val stoppingCondition: Parser<*>,
) : BaseManyParser<T>(
    parser,
    shouldStopParsingAtNodeCount = { false },
    shouldStopParsingForNext = { stoppingCondition.predict(it) },
    isValidNodeCount = { true },
    invalidNodeCountMessage = { "" }
)
