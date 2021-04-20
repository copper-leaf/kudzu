package com.copperleaf.kudzu.parser

@ExperimentalStdlibApi
class ParserException(
    message: String,
    val parser: Parser<*>,
    val input: ParserContext
) : Exception("Parse error: $message (${parser::class.simpleName} at ${input.position})")
