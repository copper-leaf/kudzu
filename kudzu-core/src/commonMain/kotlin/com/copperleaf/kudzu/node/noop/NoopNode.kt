package com.copperleaf.kudzu.node.noop

import com.copperleaf.kudzu.node.NodeContext
import com.copperleaf.kudzu.node.TerminalNode

class NoopNode(
    context: NodeContext
) : TerminalNode(context) {
    override val text: String = ""
}
