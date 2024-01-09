package com.copperleaf.kudzu.node.choice

import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.node.NodeContext
import com.copperleaf.kudzu.node.NonTerminalNode

public sealed class Choice6Node<T1 : Node, T2 : Node, T3 : Node, T4 : Node, T5 : Node, T6 : Node>(
    node: Node,
    context: NodeContext
) : NonTerminalNode(context) {
    public abstract val node: Node
    override val children: List<Node> = listOf(node)
    override val astNodeName: String get() = "Choice6Node.${this::class.simpleName!!}"

    public class Option1<T1 : Node, T2 : Node, T3 : Node, T4 : Node, T5 : Node, T6 : Node>(
        override val node: T1,
        context: NodeContext
    ) : Choice6Node<T1, T2, T3, T4, T5, T6>(node, context)

    public class Option2<T1 : Node, T2 : Node, T3 : Node, T4 : Node, T5 : Node, T6 : Node>(
        override val node: T2,
        context: NodeContext
    ) : Choice6Node<T1, T2, T3, T4, T5, T6>(node, context)

    public class Option3<T1 : Node, T2 : Node, T3 : Node, T4 : Node, T5 : Node, T6 : Node>(
        override val node: T3,
        context: NodeContext
    ) : Choice6Node<T1, T2, T3, T4, T5, T6>(node, context)

    public class Option4<T1 : Node, T2 : Node, T3 : Node, T4 : Node, T5 : Node, T6 : Node>(
        override val node: T4,
        context: NodeContext
    ) : Choice6Node<T1, T2, T3, T4, T5, T6>(node, context)

    public class Option5<T1 : Node, T2 : Node, T3 : Node, T4 : Node, T5 : Node, T6 : Node>(
        override val node: T5,
        context: NodeContext
    ) : Choice6Node<T1, T2, T3, T4, T5, T6>(node, context)

    public class Option6<T1 : Node, T2 : Node, T3 : Node, T4 : Node, T5 : Node, T6 : Node>(
        override val node: T6,
        context: NodeContext
    ) : Choice6Node<T1, T2, T3, T4, T5, T6>(node, context)
}
