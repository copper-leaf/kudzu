package com.copperleaf.kudzu.performance

import com.copperleaf.kudzu.expectThat
import com.copperleaf.kudzu.parsedCorrectly
import com.copperleaf.kudzu.parser.expression.ExpressionParser
import com.copperleaf.kudzu.parser.expression.IntAsDoubleParser
import com.copperleaf.kudzu.parser.expression.Operator
import com.copperleaf.kudzu.test
import io.kotest.core.spec.style.StringSpec
import kotlin.math.pow

class KudzuPerformanceTests : StringSpec({
    val simpleExpression = run {
        "1 - 2 * (3 + 4 / 5 ^ 6 * (7 - 8)) * 9"
    }
    val deeplyNestedExpression = run {
        val depth = 100_000
        val expresssionStart = (1..depth).joinToString(separator = "") { "1 + (" }
        val expresssionEnd = (1..depth).joinToString(separator = "") { ")" }

        "$expresssionStart 1 + 1 $expresssionEnd"
    }

    // this test takes a long time, so only run it as-needed and not part of normal development cycles
    "runPerformanceTestOnSimpleExpression".config(enabled = false) {
        performanceTest(10_000, 1000) {
            val parser = ExpressionParser<Double>(
                termParser = { IntAsDoubleParser() },

                operators = listOf(
                    Operator.Infix(op = "+", 40) { l, r -> l + r },
                    Operator.Infix(op = "-", 40) { l, r -> l - r },
                    Operator.Infix(op = "*", 60) { l, r -> l * r },
                    Operator.Infix(op = "/", 60) { l, r -> l / r },

                    Operator.Prefix(op = "-", 80) { r -> -r },
                    Operator.Infixr(op = "^", 70) { l, r -> l.pow(r) },
                )
            )

            expectThat(
                parser.test(simpleExpression, skipWhitespace = true, logErrors = true)
            ).parsedCorrectly()
        }

        println()

        performanceTest(10, 5) {
            val parser = ExpressionParser<Double>(
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

            expectThat(
                parser.test(deeplyNestedExpression, skipWhitespace = true, logErrors = true)
            ).parsedCorrectly()
        }
    }
})
