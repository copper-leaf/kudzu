package com.copperleaf.kudzu.node

import com.copperleaf.kudzu.parser.ParserContext
import com.copperleaf.kudzu.parser.SourcePosition

data class NodeContext(
    val startPosition: SourcePosition,
    val endPosition: SourcePosition
) {
    constructor(
        startContext: ParserContext,
        endContext: ParserContext
    ) : this(startContext.sourcePosition, endContext.sourcePosition)

    override fun toString(): String {
        return "NodeContext($startPosition to $endPosition)"
    }
}
