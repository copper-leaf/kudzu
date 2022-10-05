package com.copperleaf.kudzu.node.expression

import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.node.NodeContext
import com.copperleaf.kudzu.parser.expression.InfixrOperatorParser

/**
 * A Node parsed from [InfixrOperatorParser]. The start [leftOperand] has binary operations applied recursively from
 * left-to-right using [operation] for the next operator and operand.
 */
public class InfixrOperatorNode(
    public val leftOperand: Node,
    public val operation: BinaryOperationNode?,
    context: NodeContext
) : ExpressionNode(context) {
    override val children: List<Node> = listOfNotNull(leftOperand, operation)

    override val simplify: DeepRecursiveFunction<Node, Node> = DeepRecursiveFunction<Node, Node> {
        if (operation != null) {
            InfixrOperatorNode(
                simplifyChild(leftOperand),
                simplifyChild(operation) as BinaryOperationNode,
                context
            )
        } else {
            simplifyChild(leftOperand)
        }
    }
}
