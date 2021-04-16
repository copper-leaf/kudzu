package com.copperleaf.kudzu.node.maybe

import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.node.NodeContext
import com.copperleaf.kudzu.node.NonTerminalNode

class MaybeNode<T : Node>(
    val node: T?,
    context: NodeContext
) : NonTerminalNode(context) {
    override val children: List<Node> = listOfNotNull(node)
}
