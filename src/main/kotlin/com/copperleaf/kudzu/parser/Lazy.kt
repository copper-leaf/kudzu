package com.copperleaf.kudzu.parser

import com.copperleaf.kudzu.Node
import com.copperleaf.kudzu.Parser
import com.copperleaf.kudzu.ParserContext

class LazyParser(name: String = "") : Parser(name) {

    lateinit var parser: Parser

    override fun predict(input: ParserContext): Boolean = parser.predict(input)

    override fun parse(input: ParserContext): Pair<Node, ParserContext> = parser.parse(input)

}
