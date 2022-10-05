package com.copperleaf.kudzu.node.mapped

import com.copperleaf.kudzu.node.NodeContext
import com.copperleaf.kudzu.node.TerminalNode
import com.copperleaf.kudzu.parser.mapped.MappedParser

/**
 * The result of mapping a Node to a Value with [MappedParser]. The [value] does not have to be a Node. The ValueNode
 * itself is considered to be a terminal node regardless of whether the original node was terminal or not, since it
 * expects an entire sub-tree to have been reduced to a single value.
 */
public class ValueNode<T>(
    public val value: T,
    context: NodeContext
) : TerminalNode(
    context
) {
    override val text: String = value.toString()
}
