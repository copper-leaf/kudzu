package com.copperleaf.kudzu.parser

class ParserException(
    message: String,
    val parser: Parser<*>,
    val input: ParserContext
) : Exception("Parse error: $message (${parser::class.simpleName} at ${input.position})")
