package com.copperleaf.kudzu.parser.expression

import com.copperleaf.kudzu.node.Node

/**
 * Evaluates the parse-tree created by [ExpressionParser].
 */
interface ExpressionEvaluator<T : Any> {

    /**
     * Evaluate the expression starting at [Node], which should be the result of [ExpressionParser.parse].
     */
    val evaluate: DeepRecursiveFunction<Node, T>
}
