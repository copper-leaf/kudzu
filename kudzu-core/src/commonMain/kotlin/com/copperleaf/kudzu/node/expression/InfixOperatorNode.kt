package com.copperleaf.kudzu.node.expression

import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.node.NodeContext
import com.copperleaf.kudzu.node.NonTerminalNode
import com.copperleaf.kudzu.parser.expression.InfixOperatorParser

/**
 * A Node parsed from [InfixOperatorParser]. The start [leftOperand] has binary operations applied iteratively from
 * left-to-right using [operationNodes] for each subsequent operator and operand. The result of one
 * [BinaryOperationNode] becomes the left operand for the next.
 */
class InfixOperatorNode(
    val leftOperand: Node,
    val operationNodes: List<BinaryOperationNode>,
    context: NodeContext
) : NonTerminalNode(context) {
    override val children: List<Node> = listOf(leftOperand) + operationNodes
}
