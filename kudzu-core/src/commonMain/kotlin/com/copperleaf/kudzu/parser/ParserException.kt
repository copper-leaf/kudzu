package com.copperleaf.kudzu.parser

/**
 * Indicates an unrecoverable error was encountered during parsing.
 */
public class ParserException(
    public val actualMessage: String,
    public val parser: Parser<*>,
    public val input: ParserContext
) : Exception(
    """
    |Parse error at ${input.sourcePosition} (${parser::class.simpleName})
    |
    |$actualMessage
    |
    ${input.getSurroundingText(input.sourcePosition).lines().joinToString(separator = "\n") { "|$it" }}
    """.trimMargin()
)
