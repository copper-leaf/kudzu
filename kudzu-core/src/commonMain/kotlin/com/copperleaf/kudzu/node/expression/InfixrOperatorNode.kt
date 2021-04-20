package com.copperleaf.kudzu.node.expression

import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.node.NodeContext
import com.copperleaf.kudzu.node.NonTerminalNode

class InfixrOperatorNode(
    val leftOperand: Node,
    val operation: BinaryOperationNode?,
    context: NodeContext
) : NonTerminalNode(context) {
    override val children: List<Node> = listOfNotNull(leftOperand, operation)
}
