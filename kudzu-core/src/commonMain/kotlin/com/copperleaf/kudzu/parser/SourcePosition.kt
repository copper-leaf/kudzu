package com.copperleaf.kudzu.parser

public data class SourcePosition(
    val lineNumber: Int = 1,
    val lineColumn: Int = 1
) : Comparable<SourcePosition> {
    override fun toString(): String {
        return "$lineNumber:$lineColumn"
    }

    override fun compareTo(other: SourcePosition): Int {
        return compareValuesBy(this, other, { it.lineNumber }, { it.lineColumn })
    }

    public fun incrementColumn(): SourcePosition {
        return this.copy(
            lineColumn = lineColumn + 1
        )
    }

    public fun incrementRow(): SourcePosition {
        return this.copy(
            lineNumber = lineNumber + 1,
            lineColumn = 1
        )
    }
}
