package com.eden.monadik.parser

import com.eden.monadik.Node
import com.eden.monadik.Parser
import com.eden.monadik.ParserContext

class LazyParser(name: String = "") : Parser(name) {

    lateinit var parser: Parser

    override fun parse(input: ParserContext): Pair<Node, ParserContext> = parser.parse(input)

}
