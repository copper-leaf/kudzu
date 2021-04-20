package com.copperleaf.kudzu.node.chars

import com.copperleaf.kudzu.node.NodeContext
import com.copperleaf.kudzu.node.TerminalNode

class CharNode(
    private val char: Char,
    context: NodeContext
) : TerminalNode(context) {
    override val text: String = "$char"
}
