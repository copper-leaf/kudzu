package com.copperleaf.kudzu.comparison

object ParserComparisonTests {
    val enabled = false
    val simpleExpression: String get() {
        return "1 - 2 * (3 + 4 / 5 ^ 6 * (7 - 8)) * 9"
    }
    val deeplyNestedExpression: String get() {
        val depth = 100_000
        val expresssionStart = (1..depth).joinToString(separator = "") { "1 + (" }
        val expresssionEnd = (1..depth).joinToString(separator = "") { ")" }

        return "$expresssionStart 1 + 1 $expresssionEnd"
    }
}
