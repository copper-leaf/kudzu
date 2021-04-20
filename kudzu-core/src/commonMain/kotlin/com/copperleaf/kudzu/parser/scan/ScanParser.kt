package com.copperleaf.kudzu.parser.scan

import com.copperleaf.kudzu.node.NodeContext
import com.copperleaf.kudzu.node.scan.ScanNode
import com.copperleaf.kudzu.parser.Parser
import com.copperleaf.kudzu.parser.ParserContext
import com.copperleaf.kudzu.parser.ParserResult

/**
 * Consume characters from the input until its parser predicts true. The resulting [ScanNode] contains all the
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
class ScanParser(
    val next: Parser<*>,
) : Parser<ScanNode>() {

    override fun predict(input: ParserContext): Boolean {
        return true
    }

    @OptIn(ExperimentalStdlibApi::class)
    override val parse = DeepRecursiveFunction<ParserContext, ParserResult<ScanNode>> { input ->
        var remaining = input
        var text = ""

        while (remaining.isNotEmpty() && !next.predict(remaining)) {
            text += remaining.next()
            remaining = remaining.remaining()
        }

        ScanNode(text, NodeContext(input, remaining)) to remaining
    }
}
