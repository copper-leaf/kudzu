package com.copperleaf.kudzu.node.expression

import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.node.NodeContext
import com.copperleaf.kudzu.parser.expression.PrefixOperatorParser

/**
 * A Node parsed from [PrefixOperatorParser]. The start [operand] has unary operations applied iteratively from
 * left-to-right using [operatorNodes] for each subsequent operator. The result of one operation becomes the operand for
 * the next.
 */
@ExperimentalStdlibApi
class PrefixOperatorNode(
    val operatorNodes: List<Node>,
    val operand: Node,
    context: NodeContext
) : ExpressionNode(context) {
    override val children: List<Node> = operatorNodes + operand

    override val simplify = DeepRecursiveFunction<Node, Node> {
        if (operatorNodes.isEmpty()) {
            simplifyChild(operand)
        } else {
            this@PrefixOperatorNode
        }
    }
}
