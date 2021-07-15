package com.copperleaf.kudzu.node.tag

import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.node.NodeContext
import com.copperleaf.kudzu.node.NonTerminalNode

/**
 * A sequence of characters parsed from a variety of parsers, containing text parsed directly from the input.
 */
class TagNameNode<Wrapped : Node>(
    val tagName: String,
    val wrapped: Wrapped,
    context: NodeContext
) : NonTerminalNode(context) {
    override val children: List<Node> = listOf(wrapped)
}
