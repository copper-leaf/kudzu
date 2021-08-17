package com.copperleaf.kudzu.parser

/**
 * Indicates an unrecoverable error was encountered during parsing.
 */
@ExperimentalStdlibApi
class ParserException(
    val actualMessage: String,
    val parser: Parser<*>,
    val input: ParserContext
) : Exception(
    """
    |Parse error at ${input.sourcePosition} (${parser::class.simpleName})
    |
    |$actualMessage
    |
    ${input.getSurroundingText(input.sourcePosition).lines().joinToString(separator = "\n") { "|$it" }}
    """.trimMargin()
)
