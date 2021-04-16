package com.copperleaf.kudzu.parser

data class ParserContext(
    private val input: String,
    private val sourcePosition: Int,
    private val skipWhitespace: Boolean,
    private val lineNumber: Int = 1,
    private val lineColumn: Int = 1
) {

    fun isEmpty(): Boolean {
        if (skipWhitespace) {
            var idx = sourcePosition
            while (idx < input.length && input[idx].isWhitespace()) {
                idx++
            }
            return (input.length - idx) == 0
        } else {
            return (input.length - sourcePosition) == 0
        }
    }

    fun isNotEmpty(): Boolean {
        return !isEmpty()
    }

    val position: String
        get() {
            return "$lineNumber:$lineColumn"
        }

    fun next(): Char {
        val nextChar: Char
        if (skipWhitespace) {
            var idx = sourcePosition
            while (idx < input.length && input[idx].isWhitespace()) idx++
            nextChar = input[idx]
        } else {
            nextChar = input[sourcePosition]
        }

        return nextChar
    }

    fun remaining(): ParserContext {
        if (skipWhitespace) {
            var idx = sourcePosition
            var lineNumbersInc = lineNumber
            var lineColumnsInc = lineColumn
            while (idx < input.length && input[idx].isWhitespace()) {
                if (input[idx] == '\n') {
                    lineNumbersInc++
                    lineColumnsInc = 1
                } else {
                    lineColumnsInc++
                }
                idx++
            }

            return copy(sourcePosition = idx + 1, lineNumber = lineNumbersInc, lineColumn = lineColumnsInc + 1)
        } else {
            if (input[sourcePosition] == '\n') {
                return copy(sourcePosition = sourcePosition + 1, lineNumber = lineNumber + 1, lineColumn = 1)
            } else {
                return copy(sourcePosition = sourcePosition + 1, lineColumn = lineColumn + 1)
            }
        }
    }

    override fun toString(): String {
        return "ParserContext($position)"
    }

    infix fun isAfter(other: ParserContext): Boolean {
        return this.sourcePosition > other.sourcePosition
    }
}
