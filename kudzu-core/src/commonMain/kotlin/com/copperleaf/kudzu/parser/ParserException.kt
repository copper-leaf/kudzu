package com.copperleaf.kudzu.parser

/**
 * Indicates an unrecoverable error was encountered during parsing.
 */
@ExperimentalStdlibApi
class ParserException(
    message: String,
    val parser: Parser<*>,
    val input: ParserContext
) : Exception("Parse error: $message (${parser::class.simpleName} at ${input.sourcePosition})")
