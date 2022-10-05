package com.copperleaf.kudzu.node.tag

import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.node.NodeContext
import com.copperleaf.kudzu.node.NonTerminalNode

/**
 * A sequence of characters parsed from a variety of parsers, containing text parsed directly from the input.
 */
public class TagNode<Opening : Node, Content : Node, Closing : Node>(
    public val opening: TagNameNode<Opening>,
    public val content: Content,
    public val closing: TagNameNode<Closing>,
    context: NodeContext
) : NonTerminalNode(context) {
    override val children: List<Node> = listOf(opening, content, closing)
}
