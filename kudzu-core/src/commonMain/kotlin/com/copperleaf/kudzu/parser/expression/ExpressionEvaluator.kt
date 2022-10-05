package com.copperleaf.kudzu.parser.expression

import com.copperleaf.kudzu.node.Node

/**
 * Evaluates the parse-tree created by [ExpressionParser].
 */
public interface ExpressionEvaluator<T : Any> {

    /**
     * Evaluate the expression starting at [Node], which should be the result of [ExpressionParser.parse].
     */
    public val evaluate: DeepRecursiveFunction<Node, T>
}
