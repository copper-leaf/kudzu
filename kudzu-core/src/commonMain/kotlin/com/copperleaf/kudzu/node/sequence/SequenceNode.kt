package com.copperleaf.kudzu.node.sequence

import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.node.NodeContext
import com.copperleaf.kudzu.node.NonTerminalNode

class SequenceNode(
    override val children: List<Node>,
    context: NodeContext
) : NonTerminalNode(context)
