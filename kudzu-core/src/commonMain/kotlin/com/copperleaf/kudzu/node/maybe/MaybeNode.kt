package com.copperleaf.kudzu.node.maybe

import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.node.NodeContext
import com.copperleaf.kudzu.node.NonTerminalNode
import com.copperleaf.kudzu.parser.maybe.MaybeParser

/**
 * A Node parsed from a [MaybeParser]. If the sub-parser sucessfully parsed a result, [node] will be non-null and
 * contain its parser result. Otherwise, the parsing succeeded and will continue, but [node] will be null.
 */
class MaybeNode<T : Node>(
    val node: T?,
    context: NodeContext
) : NonTerminalNode(context) {
    override val children: List<Node> = listOfNotNull(node)
}
