package com.copperleaf.kudzu.node.expression

import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.node.NodeContext

/**
 * A Node representing a binary operation. It is applied to another operand node.
 *
 * @see [InfixOperatorNode]
 * @see [InfixrOperatorNode]
 */
public class BinaryOperationNode(
    public val operator: Node,
    public val operand: Node,
    context: NodeContext
) : ExpressionNode(context) {
    override val children: List<Node> = listOf(operator, operand)

    override val simplify: DeepRecursiveFunction<Node, Node> = DeepRecursiveFunction<Node, Node> {
        BinaryOperationNode(
            operator,
            simplifyChild(operand),
            context
        )
    }
}
