package com.copperleaf.kudzu.node

import com.copperleaf.kudzu.parser.ParserContext

data class NodeContext(val startContext: ParserContext, val endContext: ParserContext) {
    override fun toString(): String {
        return "NodeContext($startContext to $endContext)"
    }
}
