package com.copperleaf.kudzu.node.choice

import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.node.NodeContext
import com.copperleaf.kudzu.node.NonTerminalNode

sealed class Choice4Node<T1 : Node, T2 : Node, T3 : Node, T4 : Node>(
    val node: Node,
    context: NodeContext
) : NonTerminalNode(context) {
    override val children: List<Node> = listOf(node)
    override val astNodeName: String get() = "Choice4Node.${this::class.simpleName!!}"

    class Option1<T1 : Node, T2 : Node, T3 : Node, T4 : Node>(
        node: T1,
        context: NodeContext
    ) : Choice4Node<T1, T2, T3, T4>(node, context)
    class Option2<T1 : Node, T2 : Node, T3 : Node, T4 : Node>(
        node: T2,
        context: NodeContext
    ) : Choice4Node<T1, T2, T3, T4>(node, context)
    class Option3<T1 : Node, T2 : Node, T3 : Node, T4 : Node>(
        node: T3,
        context: NodeContext
    ) : Choice4Node<T1, T2, T3, T4>(node, context)
    class Option4<T1 : Node, T2 : Node, T3 : Node, T4 : Node>(
        node: T4,
        context: NodeContext
    ) : Choice4Node<T1, T2, T3, T4>(node, context)
}
