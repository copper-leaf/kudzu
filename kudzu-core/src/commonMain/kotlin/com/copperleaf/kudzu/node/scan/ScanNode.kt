package com.copperleaf.kudzu.node.scan

import com.copperleaf.kudzu.node.NodeContext
import com.copperleaf.kudzu.node.TerminalNode

class ScanNode(
    override val text: String,
    context: NodeContext
) : TerminalNode(context)
