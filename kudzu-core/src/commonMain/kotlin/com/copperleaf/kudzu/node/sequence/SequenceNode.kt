package com.copperleaf.kudzu.node.sequence

import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.node.NodeContext
import com.copperleaf.kudzu.node.NonTerminalNode

class SequenceNode(
    val nodeList: List<Node>,
    context: NodeContext
) : NonTerminalNode(context) {
    override val children: List<Node> = nodeList
}
