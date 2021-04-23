package com.copperleaf.kudzu.node.expression

import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.node.NodeContext
import com.copperleaf.kudzu.parser.expression.InfixrOperatorParser

/**
 * A Node parsed from [InfixrOperatorParser]. The start [leftOperand] has binary operations applied recursively from
 * left-to-right using [operation] for the next operator and operand.
 */
class InfixrOperatorNode(
    val leftOperand: Node,
    val operation: BinaryOperationNode?,
    context: NodeContext
) : ExpressionNode(context) {
    override val children: List<Node> = listOfNotNull(leftOperand, operation)

    override fun simplify(): Node {
        return if (operation != null) {
            InfixrOperatorNode(
                leftOperand.simplifyChild(),
                operation.simplifyChild() as BinaryOperationNode,
                context
            )
        } else {
            leftOperand.simplifyChild()
        }
    }
}
