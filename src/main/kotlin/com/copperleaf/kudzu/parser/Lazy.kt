package com.copperleaf.kudzu.parser

import com.copperleaf.kudzu.Node
import com.copperleaf.kudzu.Parser
import com.copperleaf.kudzu.ParserContext

/**
 * A shim to allow parsers to be mutually recursive. Unlike most parsers, the Lazy parser does not do anything on its
 * own or return its own node. At parse-time, it acts as as if it is its parser, and not like it wraps its parser.
 *
 * Predicts true when:
 *   - its parser predicts true
 *
 * Parsing fails when:
 *   - its parser fails to parse
 */
class LazyParser(name: String = "") : Parser(name) {

    lateinit var parser: Parser

    override fun predict(input: ParserContext): Boolean = parser.predict(input)

    override fun parse(input: ParserContext): Pair<Node, ParserContext> = parser.parse(input)

}
