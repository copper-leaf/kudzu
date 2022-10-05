package com.copperleaf.kudzu.node.expression

import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.node.NodeContext

/**
 * A Node representing a binary operation. It is applied to another operand node.
 *
 * @see [InfixOperatorNode]
 * @see [InfixrOperatorNode]
 */
public class RootExpressionNode(
    public val expressionNode: Node,
    context: NodeContext
) : ExpressionNode(context) {
    override val children: List<Node> = listOf(expressionNode)

    override val simplify: DeepRecursiveFunction<Node, Node> = DeepRecursiveFunction<Node, Node> {
        RootExpressionNode(
            simplifyChild(expressionNode),
            context
        )
    }
}
