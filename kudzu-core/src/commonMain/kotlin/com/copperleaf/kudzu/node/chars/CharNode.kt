package com.copperleaf.kudzu.node.chars

import com.copperleaf.kudzu.node.NodeContext
import com.copperleaf.kudzu.node.TerminalNode
import com.copperleaf.kudzu.parser.chars.BaseCharParser

/**
 * A Node parsed from a [BaseCharParser] containing a single character parsed from the input.
 */
class CharNode(
    private val char: Char,
    context: NodeContext
) : TerminalNode(context) {
    override val text: String = "$char"
}
