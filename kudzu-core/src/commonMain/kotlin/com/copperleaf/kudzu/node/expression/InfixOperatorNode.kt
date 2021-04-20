package com.copperleaf.kudzu.node.expression

import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.node.NodeContext
import com.copperleaf.kudzu.node.NonTerminalNode

class InfixOperatorNode(
    val leftOperand: Node,
    val operationNodes: List<BinaryOperationNode>,
    context: NodeContext
) : NonTerminalNode(context) {
    override val children: List<Node> = listOf(leftOperand) + operationNodes
}
