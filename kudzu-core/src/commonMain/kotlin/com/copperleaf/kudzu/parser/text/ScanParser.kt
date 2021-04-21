package com.copperleaf.kudzu.parser.text

import com.copperleaf.kudzu.node.NodeContext
import com.copperleaf.kudzu.node.text.TextNode
import com.copperleaf.kudzu.parser.Parser
import com.copperleaf.kudzu.parser.ParserContext
import com.copperleaf.kudzu.parser.ParserResult

/**
 * Consume characters from the input until its parser predicts true. The resulting [TextNode] contains all the
 * characters that were consumed as a single text String.
 *
 * Predicts true when:
 *   - always
 *
 * Parsing stops when:
 *   - its parser predicts true
 *   - there is no more input remaining
 *
 * Parsing fails when:
 *   - its parser fails to parse
 */
@ExperimentalStdlibApi
class ScanParser(
    val next: Parser<*>,
) : Parser<TextNode> {

    override fun predict(input: ParserContext): Boolean {
        return true
    }

    override val parse = DeepRecursiveFunction<ParserContext, ParserResult<TextNode>> { input ->
        var remaining = input
        var text = ""

        while (!remaining.isEmpty() && !next.predict(remaining)) {
            text += remaining.next()
            remaining = remaining.remaining()
        }

        TextNode(text, NodeContext(input, remaining)) to remaining
    }
}
