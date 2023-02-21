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

    override fun nextChar(): Pair<Char, ParserContext> {
        return getNextChar() to remaining()
    }

    private fun getNextChar(): Char {
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

    private fun remaining(): ParserContext {
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

    override fun validateNextChar(fn: (Char) -> Boolean): Boolean {
        return if (this.isEmpty()) {
            false
        } else {
            fn(getNextChar())
        }
    }

// getSurroundingText
// ---------------------------------------------------------------------------------------------------------------------

    companion object {
        private const val surroundingPipe = '|'
        private const val surroundingRightArrow = '>'
        private const val surroundingUpArrow = '^'
    }

    override fun getSurroundingText(position: SourcePosition): String {
        val lines = this.input.lines()
        check((position.lineNumber - 1) in lines.indices)

        return buildString {
            val lineIndices: Iterable<Int> = when {
                position.lineNumber == 1 -> {
                    1..3
                }
                position.lineNumber == lines.size -> {
                    lines.indices.toList().takeLast(3).map { it + 1 }
                }
                else -> {
                    (position.lineNumber - 1)..(position.lineNumber + 1)
                }
            }

            val lineNumberColumnWidth = lineIndices.maxOrNull().toString().length

            lineIndices
                .asSequence()
                .filter { (it - 1) in lines.indices }
                .forEach {
                    appendContentLine(it, lineNumberColumnWidth, lines)
                    if (position.lineNumber == it) {
                        appendPointerLine(position.lineColumn, lineNumberColumnWidth)
                    }
                }
            removeSuffix('\n')
        }
    }

    private fun StringBuilder.appendContentLine(
        lineNumber: Int,
        columnWidth: Int,
        lines: List<String>,
    ) {
        append(lineNumber.toString().padStart(columnWidth))
        append(surroundingPipe)
        appendLine(lines[lineNumber - 1])
    }

    private fun StringBuilder.appendPointerLine(
        lineWidth: Int,
        columnWidth: Int,
    ) {
        repeat(lineWidth + columnWidth) { append(surroundingRightArrow) }
        appendLine(surroundingUpArrow)
    }

    private fun StringBuilder.removeSuffix(suffix: Char): StringBuilder {
        if (endsWith(suffix)) {
            deleteAt(lastIndex)
        }
        return this
    }

    override fun toString(): String {
        return "ParserContextImpl($sourcePosition)"
    }
}
