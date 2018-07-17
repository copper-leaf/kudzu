package com.eden.monadik

class LazyParser : Parser() {

    lateinit var parser: Parser

    override fun parse(input: ParsingContext): Pair<Node, ParsingContext> = parser.parse(input)

}
