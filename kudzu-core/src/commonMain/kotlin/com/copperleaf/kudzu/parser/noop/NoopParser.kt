package com.copperleaf.kudzu.parser.noop

import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.node.NodeContext
import com.copperleaf.kudzu.node.noop.NoopNode
import com.copperleaf.kudzu.parser.Parser
import com.copperleaf.kudzu.parser.ParserContext
import com.copperleaf.kudzu.parser.ParserResult
import com.copperleaf.kudzu.parser.tag.TagBuilder
import com.copperleaf.kudzu.parser.tag.TagParser

/**
 * A parser that always predicts true, and consumes no input.
 *
 * This can be useful for creating "self-closing" tags with the [TagParser]. By using a [NoopParser] for
 * [TagBuilder.closingParser], no content will be scanned, and no specific sequence of characters is needed to mark the
 * end of the tag, since scanning continues until `closingParser` predicts true, and then expects `closingParser` to
 * parse to completion.
 */
class NoopParser : Parser<Node> {
    override fun predict(input: ParserContext): Boolean {
        return true
    }

    override val parse = DeepRecursiveFunction<ParserContext, ParserResult<Node>> { input ->
        NoopNode(NodeContext(input, input)) to input
    }
}
