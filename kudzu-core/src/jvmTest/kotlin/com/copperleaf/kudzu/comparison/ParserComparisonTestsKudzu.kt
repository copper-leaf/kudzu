package com.copperleaf.kudzu.comparison

import com.copperleaf.kudzu.comparison.ParserComparisonTests.deeplyNestedExpression
import com.copperleaf.kudzu.comparison.ParserComparisonTests.simpleExpression
import com.copperleaf.kudzu.expectThat
import com.copperleaf.kudzu.parsedCorrectly
import com.copperleaf.kudzu.parser.expression.ExpressionParser
import com.copperleaf.kudzu.parser.expression.IntAsDoubleParser
import com.copperleaf.kudzu.parser.expression.Operator
import com.copperleaf.kudzu.performance.performanceTest
import com.copperleaf.kudzu.test
import io.kotest.core.spec.style.StringSpec
import kotlin.math.pow

@Suppress("UNUSED_VARIABLE")
class ParserComparisonTestsKudzu : StringSpec({

    // this test takes a long time, so only run it as-needed and not part of normal development cycles
    "kudzuSimpleExpression".config(enabled = ParserComparisonTests.enabled) {
        performanceTest(10_000, 1000) {
            expectThat(
                kudzuParser.test(simpleExpression, skipWhitespace = true, logErrors = true)
            ).parsedCorrectly()
        }
    }

    // this test takes a long time, so only run it as-needed and not part of normal development cycles
    "kudzuDeeplyNestedExpression".config(enabled = ParserComparisonTests.enabled) {
        performanceTest(1, 0) {
            expectThat(
                kudzuParser.test(deeplyNestedExpression, skipWhitespace = true, logErrors = true)
            ).parsedCorrectly()
        }
    }
})

val kudzuParser = ExpressionParser<Double>(
    termParser = { IntAsDoubleParser() },
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
