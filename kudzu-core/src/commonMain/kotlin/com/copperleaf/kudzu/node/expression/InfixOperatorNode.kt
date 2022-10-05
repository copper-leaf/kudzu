package com.copperleaf.kudzu.node.expression

import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.node.NodeContext
import com.copperleaf.kudzu.parser.expression.InfixOperatorParser

/**
 * A Node parsed from [InfixOperatorParser]. The start [leftOperand] has binary operations applied iteratively from
 * left-to-right using [operationNodes] for each subsequent operator and operand. The result of one
 * [BinaryOperationNode] becomes the left operand for the next.
 */
public class InfixOperatorNode(
    public val leftOperand: Node,
    public val operationNodes: List<BinaryOperationNode>,
    context: NodeContext
) : ExpressionNode(context) {
    override val children: List<Node> = listOf(leftOperand) + operationNodes

    override val simplify: DeepRecursiveFunction<Node, Node> = DeepRecursiveFunction<Node, Node> {
        if (operationNodes.isEmpty()) {
            simplifyChild(leftOperand)
        } else {
            InfixOperatorNode(
                simplifyChild(leftOperand),
                operationNodes.map { simplifyChild(it) as BinaryOperationNode },
                context
            )
        }
    }
}
