package com.copperleaf.kudzu.node.text

import com.copperleaf.kudzu.node.NodeContext
import com.copperleaf.kudzu.node.TerminalNode

/**
 * A sequence of characters parsed from a variety of parsers, containing text parsed directly from the input.
 */
public class TextNode(
    override val text: String,
    context: NodeContext
) : TerminalNode(context)
