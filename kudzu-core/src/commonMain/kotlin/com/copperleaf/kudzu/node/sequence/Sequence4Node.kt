package com.copperleaf.kudzu.node.sequence

import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.node.NodeContext
import com.copperleaf.kudzu.node.NonTerminalNode

/**
 * A version of [SequenceNNode] with fixed size that retains its generic type information for each parser in the sequence.
 */
class Sequence4Node<T1 : Node, T2 : Node, T3 : Node, T4 : Node>(
    val node1: T1,
    val node2: T2,
    val node3: T3,
    val node4: T4,
    context: NodeContext
) : NonTerminalNode(context) {
    override val children: List<Node> = listOf(
        node1,
        node2,
        node3,
        node4,
    )

    operator fun component1(): NodeContext = context
    operator fun component2(): T1 = node1
    operator fun component3(): T2 = node2
    operator fun component4(): T3 = node3
    operator fun component5(): T4 = node4
}
