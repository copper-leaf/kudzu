package com.copperleaf.kudzu.node.choice

import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.node.NodeContext
import com.copperleaf.kudzu.node.NonTerminalNode

sealed class Choice2Node<T1 : Node, T2 : Node>(
    node: Node,
    context: NodeContext
) : NonTerminalNode(context) {
    abstract val node: Node
    override val children: List<Node> = listOf(node)
    override val astNodeName: String get() = "Choice2Node.${this::class.simpleName!!}"

    class Option1<T1 : Node, T2 : Node>(
        override val node: T1,
        context: NodeContext
    ) : Choice2Node<T1, T2>(node, context)
    class Option2<T1 : Node, T2 : Node>(
        override val node: T2,
        context: NodeContext
    ) : Choice2Node<T1, T2>(node, context)
}
