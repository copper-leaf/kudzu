package com.copperleaf.kudzu.node.expression

import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.node.NodeContext

/**
 * A Node representing a binary operation. It is applied to another operand node.
 *
 * @see [InfixOperatorNode]
 * @see [InfixrOperatorNode]
 */
class BinaryOperationNode(
    val operator: Node,
    val operand: Node,
    context: NodeContext
) : ExpressionNode(context) {
    override val children: List<Node> = listOf(operator, operand)

    override fun simplify(): Node {
        return BinaryOperationNode(
            operator,
            operand.simplifyChild(),
            context
        )
    }
}
