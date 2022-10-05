package com.copperleaf.kudzu.node.named

import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.node.NonTerminalNode
import com.copperleaf.kudzu.parser.mapped.MappedParser
import com.copperleaf.kudzu.parser.named.NamedParser
import com.copperleaf.kudzu.visitor.Visitor

/**
 * A Node parsed from a [NamedParser]. This is used to annotate a particular point in the tree and make it easier to
 * find that specific point later during visitation or mapping.
 *
 * @see [Visitor]
 * @see [MappedParser]
 */
public class NamedNode<T : Node>(
    public val node: T,
    public val name: String
) : NonTerminalNode(node.context) {
    override val children: List<Node> = listOf(node)

    override val astNodeName: String get() = "${super.astNodeName}:$name"
}
