package com.copperleaf.kudzu.node.sequence

import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.node.NodeContext
import com.copperleaf.kudzu.node.NonTerminalNode
import com.copperleaf.kudzu.parser.sequence.SequenceNParser

/**
 * A Node parsed from a [SequenceNParser]. The SequenceNode contains exactly 1 Node from each of its sub-parsers in the
 * same order they were parsed. Thus, you can guarantee that the first child parser of [SequenceNParser] produces a Node
 * at index 0 of [children], the 2nd parser produces a node at [children] index 1, etc.
 */
public class SequenceNNode(
    override val children: List<Node>,
    context: NodeContext
) : NonTerminalNode(context) {

    public operator fun component1(): NodeContext = context
    public operator fun component2(): Node = children[0]
    public operator fun component3(): Node = children[1]
    public operator fun component4(): Node = children[2]
    public operator fun component5(): Node = children[3]
    public operator fun component6(): Node = children[4]
    public operator fun component7(): Node = children[5]
    public operator fun component8(): Node = children[6]
    public operator fun component9(): Node = children[7]
    public operator fun component10(): Node = children[8]
}
