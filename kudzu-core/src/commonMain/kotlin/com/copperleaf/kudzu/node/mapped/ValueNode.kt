package com.copperleaf.kudzu.node.mapped

import com.copperleaf.kudzu.node.NodeContext
import com.copperleaf.kudzu.node.TerminalNode

class ValueNode<T : Any>(
    val value: T,
    context: NodeContext
) : TerminalNode(
    context
) {
    override val text: String = value.toString()
}
