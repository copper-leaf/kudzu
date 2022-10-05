package com.copperleaf.kudzu.node.many

import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.node.NodeContext
import com.copperleaf.kudzu.node.NonTerminalNode
import com.copperleaf.kudzu.parser.many.BaseManyParser

/**
 * A Node parsed from a [BaseManyParser]. The [nodeList] contains exactly as many nodes as were allowed to parse, which
 * are all of the same type since they came from the same parser.
 */
public class ManyNode<T : Node>(
    public val nodeList: List<T>,
    context: NodeContext
) : NonTerminalNode(context) {
    override val children: List<Node> = nodeList
}
