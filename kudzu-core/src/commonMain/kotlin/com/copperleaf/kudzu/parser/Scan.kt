package com.copperleaf.kudzu.parser

import com.copperleaf.kudzu.Node
import com.copperleaf.kudzu.NodeContext
import com.copperleaf.kudzu.Parser
import com.copperleaf.kudzu.ParserContext
import com.copperleaf.kudzu.TerminalNode

class ScanNode(override val text: String, name: String, context: NodeContext) : TerminalNode(name, context)

/**
 * Consume characters from the input until its parser predicts true.
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
class ScanParser(val next: Parser, name: String = "") : Parser(name) {

    override fun predict(input: ParserContext): Boolean {
        return true
    }

    override fun parse(input: ParserContext): Pair<Node, ParserContext> {
        var remaining = input
        var text = ""

        while (remaining.isNotEmpty() && !next.predict(remaining)) {
            text += remaining.next()
            remaining = remaining.remaining()
        }

        return Pair(ScanNode(text, name, NodeContext(input, remaining)), remaining)
    }
}
