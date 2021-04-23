package com.copperleaf.kudzu.node.expression

import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.node.NodeContext
import com.copperleaf.kudzu.parser.expression.PostfixOperatorParser

/**
 * A Node parsed from [PostfixOperatorParser]. The start [operand] has unary operations applied iteratively from
 * left-to-right using [operatorNodes] for each subsequent operator. The result of one operation becomes the operand for
 * the next.
 */
class PostfixOperatorNode(
    val operand: Node,
    val operatorNodes: List<Node>,
    context: NodeContext
) : ExpressionNode(context) {
    override val children: List<Node> = listOf(operand) + operatorNodes

    override fun simplify(): Node {
        return if(operatorNodes.isEmpty()) {
            operand.simplifyChild()
        } else {
            this
        }
    }
}
