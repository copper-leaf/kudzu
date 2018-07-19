package com.eden.monadik

class LazyParser(name: String = "") : Parser(name) {

    lateinit var parser: Parser

    override fun parse(input: ParserContext): Pair<Node, ParserContext> = parser.parse(input)

}
