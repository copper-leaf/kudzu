package com.copperleaf.kudzu.comparison

import com.copperleaf.kudzu.expectThat
import com.copperleaf.kudzu.parsedCorrectly
import com.copperleaf.kudzu.parser.expression.ExpressionParser
import com.copperleaf.kudzu.parser.expression.Operator
import com.copperleaf.kudzu.parser.expression.TestExpression
import com.copperleaf.kudzu.performance.performanceTest
import com.copperleaf.kudzu.test
import kotlin.math.pow
import kotlin.test.Test

@Suppress("UNUSED_VARIABLE")
class ParserComparisonTestsKudzu : ParserComparisonTests() {

    val kudzuParser = ExpressionParser<Double>(
        termParser = { TestExpression.IntAsDoubleParser() },
        simplifyAst = false,

        operators = listOf(
            Operator.Infix(op = "+", 40) { l, r -> l + r },
            Operator.Infix(op = "-", 40) { l, r -> l - r },
            Operator.Infix(op = "*", 60) { l, r -> l * r },
            Operator.Infix(op = "/", 60) { l, r -> l / r },

            Operator.Prefix(op = "-", 80) { r -> -r },
            Operator.Infixr(op = "^", 70) { l, r -> l.pow(r) },
        )
    )

    @Test // this test takes a long time, so only run it as-needed and not part of normal development cycles
    fun kudzuSimpleExpression() {
        if (!enabled) return

        performanceTest(10_000, 1000) {
            expectThat(
                kudzuParser.test(simpleExpression, skipWhitespace = true, logErrors = true)
            ).parsedCorrectly()
        }
    }

    @Test // this test takes a long time, so only run it as-needed and not part of normal development cycles
    fun kudzuDeeplyNestedExpression() {
        if (!enabled) return

        performanceTest(1, 0) {
            expectThat(
                kudzuParser.test(deeplyNestedExpression, skipWhitespace = true, logErrors = true)
            ).parsedCorrectly()
        }
    }
}
