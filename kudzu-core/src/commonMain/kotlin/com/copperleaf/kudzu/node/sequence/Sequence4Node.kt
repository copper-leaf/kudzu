package com.copperleaf.kudzu.node.sequence

import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.node.NodeContext
import com.copperleaf.kudzu.node.NonTerminalNode

/**
 * A version of [SequenceNNode] with fixed size that retains its generic type information for each parser in the sequence.
 */
public class Sequence4Node<T1 : Node, T2 : Node, T3 : Node, T4 : Node>(
    public val node1: T1,
    public val node2: T2,
    public val node3: T3,
    public val node4: T4,
    context: NodeContext
) : NonTerminalNode(context) {
    override val children: List<Node> = listOf(
        node1,
        node2,
        node3,
        node4,
    )

    public operator fun component1(): NodeContext = context
    public operator fun component2(): T1 = node1
    public operator fun component3(): T2 = node2
    public operator fun component4(): T3 = node3
    public operator fun component5(): T4 = node4
}
