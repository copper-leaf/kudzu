package com.copperleaf.kudzu.node

import com.copperleaf.kudzu.parser.ParserContext
import com.copperleaf.kudzu.parser.SourcePosition

/**
 * Contains the start and end positions of the input that this Node was parsed from. For terminal nodes, this will be
 * exactly the source positions surrounding the text parsed. For non-terminal nodes, the [startPosition] is the position
 * before any parsing started and the [endPosition] is the position after all children parsers have finished, and the
 * range of the parent node completely encapsulates the ranges of all its children nodes which will fall somewhere in
 * the parent's range.
 */
public data class NodeContext(
    val startPosition: SourcePosition,
    val endPosition: SourcePosition
) {
    public constructor(
        startContext: ParserContext,
        endContext: ParserContext
    ) : this(startContext.sourcePosition, endContext.sourcePosition)

    override fun toString(): String {
        return "NodeContext($startPosition to $endPosition)"
    }
}
