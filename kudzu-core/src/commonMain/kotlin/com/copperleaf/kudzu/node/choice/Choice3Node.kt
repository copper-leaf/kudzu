package com.copperleaf.kudzu.node.choice

import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.node.NodeContext
import com.copperleaf.kudzu.node.NonTerminalNode

sealed class Choice3Node<T1 : Node, T2 : Node, T3 : Node>(
    val node: Node,
    context: NodeContext
) : NonTerminalNode(context) {
    override val children: List<Node> = listOf(node)
    override val astNodeName: String get() = "Choice3Node.${this::class.simpleName!!}"

    class Option1<T1 : Node, T2 : Node, T3 : Node>(
        node: T1,
        context: NodeContext
    ) : Choice3Node<T1, T2, T3>(node, context)
    class Option2<T1 : Node, T2 : Node, T3 : Node>(
        node: T2,
        context: NodeContext
    ) : Choice3Node<T1, T2, T3>(node, context)
    class Option3<T1 : Node, T2 : Node, T3 : Node>(
        node: T3,
        context: NodeContext
    ) : Choice3Node<T1, T2, T3>(node, context)
}
