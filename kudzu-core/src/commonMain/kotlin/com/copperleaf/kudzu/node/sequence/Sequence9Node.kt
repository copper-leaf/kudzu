package com.copperleaf.kudzu.node.sequence

import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.node.NodeContext
import com.copperleaf.kudzu.node.NonTerminalNode

/**
 * A version of [SequenceNNode] with fixed size that retains its generic type information for each parser in the sequence.
 */
class Sequence9Node<T1 : Node, T2 : Node, T3 : Node, T4 : Node, T5 : Node, T6 : Node, T7 : Node, T8 : Node, T9 : Node>(
    val node1: T1,
    val node2: T2,
    val node3: T3,
    val node4: T4,
    val node5: T5,
    val node6: T6,
    val node7: T7,
    val node8: T8,
    val node9: T9,
    context: NodeContext
) : NonTerminalNode(context) {
    override val children: List<Node> = listOf(
        node1,
        node2,
        node3,
        node4,
        node5,
        node6,
        node7,
        node8,
        node9
    )

    operator fun component1(): NodeContext = context
    operator fun component2(): T1 = node1
    operator fun component3(): T2 = node2
    operator fun component4(): T3 = node3
    operator fun component5(): T4 = node4
    operator fun component6(): T5 = node5
    operator fun component7(): T6 = node6
    operator fun component8(): T7 = node7
    operator fun component9(): T8 = node8
    operator fun component10(): T9 = node9
}
