package com.copperleaf.kudzu.parser

/**
 * The default, in-memory implementation of an immutable [ParserContext].
 */
internal data class ParserContextImpl(
    private val input: String,
    private val index: Int = 0,
    private val skipWhitespace: Boolean = false,
    override val sourcePosition: SourcePosition = SourcePosition()
) : ParserContext {

    override fun isEmpty(): Boolean {
        if (skipWhitespace) {
            var idx = index
            while (idx < input.length && input[idx].isWhitespace()) {
                idx++
            }
            return (input.length - idx) == 0
        } else {
            return (input.length - index) == 0
        }
    }

    override fun next(): Char {
        val nextChar: Char
        if (skipWhitespace) {
            var idx = index
            while (idx < input.length && input[idx].isWhitespace()) idx++
            nextChar = input[idx]
        } else {
            nextChar = input[index]
        }

        return nextChar
    }

    override fun remaining(): ParserContext {
        if (skipWhitespace) {
            var idx = index
            var lineNumbersInc = sourcePosition.lineNumber
            var lineColumnsInc = sourcePosition.lineColumn
            while (idx < input.length && input[idx].isWhitespace()) {
                if (input[idx] == '\n') {
                    lineNumbersInc++
                    lineColumnsInc = 1
                } else {
                    lineColumnsInc++
                }
                idx++
            }

            return copy(
                index = idx + 1,
                sourcePosition = SourcePosition(
                    lineNumber = lineNumbersInc,
                    lineColumn = lineColumnsInc + 1
                )
            )
        } else {
            if (input[index] == '\n') {
                return copy(
                    index = index + 1,
                    sourcePosition = sourcePosition.incrementRow()
                )
            } else {
                return copy(
                    index = index + 1,
                    sourcePosition = sourcePosition.incrementColumn()
                )
            }
        }
    }

    override fun toString(): String {
        return "ParserContextImpl($sourcePosition)"
    }
}
