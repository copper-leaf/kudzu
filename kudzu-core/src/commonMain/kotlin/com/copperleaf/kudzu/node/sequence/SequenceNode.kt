package com.copperleaf.kudzu.node.sequence

import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.node.NodeContext
import com.copperleaf.kudzu.node.NonTerminalNode
import com.copperleaf.kudzu.parser.sequence.SequenceParser

/**
 * A Node parsed from a [SequenceParser]. The SequenceNode contains exactly 1 Node from each of its sub-parsers in the
 * same order they were parsed. Thus, you can guarantee that the first child parser of [SequenceParser] produces a Node
 * at index 0 of [children], the 2nd parser produces a node at [children] index 1, etc.
 */
class SequenceNode(
    override val children: List<Node>,
    context: NodeContext
) : NonTerminalNode(context)
