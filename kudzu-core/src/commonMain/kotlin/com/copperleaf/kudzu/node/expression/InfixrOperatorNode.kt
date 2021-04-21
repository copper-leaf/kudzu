package com.copperleaf.kudzu.node.expression

import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.node.NodeContext
import com.copperleaf.kudzu.node.NonTerminalNode
import com.copperleaf.kudzu.parser.expression.InfixrOperatorParser

/**
 * A Node parsed from [InfixrOperatorParser]. The start [leftOperand] has binary operations applied recursively from
 * left-to-right using [operation] for the next operator and operand.
 */
class InfixrOperatorNode(
    val leftOperand: Node,
    val operation: BinaryOperationNode?,
    context: NodeContext
) : NonTerminalNode(context) {
    override val children: List<Node> = listOfNotNull(leftOperand, operation)
}
