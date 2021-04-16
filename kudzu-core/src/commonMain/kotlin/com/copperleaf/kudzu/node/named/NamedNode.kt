package com.copperleaf.kudzu.node.named

import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.node.NonTerminalNode

class NamedNode<T : Node>(
    val node: T,
    val name: String
) : NonTerminalNode(node.context) {
    override val children: List<Node> = listOf(node)

    override val astNodeName: String get() = "${super.astNodeName}:$name"
}
