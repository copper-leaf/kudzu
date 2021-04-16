package com.copperleaf.kudzu.node.text

import com.copperleaf.kudzu.node.NodeContext
import com.copperleaf.kudzu.node.TerminalNode

class TextNode(
    override val text: String,
    context: NodeContext
) : TerminalNode(context)
