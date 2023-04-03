package com.copperleaf.kudzu.comparison

abstract class ParserComparisonTests {

    protected val enabled = false

// Test Cases
// ---------------------------------------------------------------------------------------------------------------------

    protected val simpleExpression = run {
        "1 - 2 * (3 + 4 / 5 ^ 6 * (7 - 8)) * 9"
    }
    protected val deeplyNestedExpression = run {
        val depth = 100_000
        val expresssionStart = (1..depth).joinToString(separator = "") { "1 + (" }
        val expresssionEnd = (1..depth).joinToString(separator = "") { ")" }

        "$expresssionStart 1 + 1 $expresssionEnd"
    }
}
