package com.copperleaf.kudzu.node.many

import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.node.NodeContext
import com.copperleaf.kudzu.node.NonTerminalNode

class ManyNode<T: Node>(
    private val nodeList: List<T>,
    context: NodeContext
) : NonTerminalNode(context) {
    override val children: List<Node> = nodeList
}
