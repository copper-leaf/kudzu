package com.copperleaf.kudzu.node.choice

import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.node.NodeContext
import com.copperleaf.kudzu.node.NonTerminalNode
import com.copperleaf.kudzu.parser.choice.ExactChoiceParser
import com.copperleaf.kudzu.parser.choice.PredictiveChoiceParser
import com.copperleaf.kudzu.parser.named.NamedParser

/**
 * A Node parsed from a [ExactChoiceParser] or [PredictiveChoiceParser]. The ChoiceNode itself gives no indicitation of
 * which sub-parser succeeded, it simply contains the node that was properly parsed. If you need to know specifically
 * which parser produced this [node], it should be annotated with [NamedParser].
 */
class ChoiceNode(
    val node: Node,
    context: NodeContext
) : NonTerminalNode(context) {
    override val children: List<Node> = listOf(node)
}
