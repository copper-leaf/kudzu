package com.copperleaf.kudzu.node.choice

import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.node.NodeContext
import com.copperleaf.kudzu.node.NonTerminalNode

sealed class Choice5Node<T1 : Node, T2 : Node, T3 : Node, T4 : Node, T5 : Node>(
    node: Node,
    context: NodeContext
) : NonTerminalNode(context) {
    abstract val node: Node
    override val children: List<Node> = listOf(node)
    override val astNodeName: String get() = "Choice5Node.${this::class.simpleName!!}"

    class Option1<T1 : Node, T2 : Node, T3 : Node, T4 : Node, T5 : Node>(
        override val node: T1,
        context: NodeContext
    ) : Choice5Node<T1, T2, T3, T4, T5>(node, context)
    class Option2<T1 : Node, T2 : Node, T3 : Node, T4 : Node, T5 : Node>(
        override val node: T2,
        context: NodeContext
    ) : Choice5Node<T1, T2, T3, T4, T5>(node, context)
    class Option3<T1 : Node, T2 : Node, T3 : Node, T4 : Node, T5 : Node>(
        override val node: T3,
        context: NodeContext
    ) : Choice5Node<T1, T2, T3, T4, T5>(node, context)
    class Option4<T1 : Node, T2 : Node, T3 : Node, T4 : Node, T5 : Node>(
        override val node: T4,
        context: NodeContext
    ) : Choice5Node<T1, T2, T3, T4, T5>(node, context)
    class Option5<T1 : Node, T2 : Node, T3 : Node, T4 : Node, T5 : Node>(
        override val node: T5,
        context: NodeContext
    ) : Choice5Node<T1, T2, T3, T4, T5>(node, context)
}
