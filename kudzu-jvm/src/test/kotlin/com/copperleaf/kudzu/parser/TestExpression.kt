package com.copperleaf.kudzu.parser

import com.copperleaf.kudzu.node
import com.copperleaf.kudzu.parsedCorrectly
import com.copperleaf.kudzu.visit
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isNotNull
import kotlin.math.pow

class TestExpression {

    @TestFactory
    fun testExpressionParser(): List<DynamicTest> {
        val operators = listOf<EvaluatableOperator<Double>>(
                InfixEvaluatableOperator(CharInParser('+', name = "+"), 40) { lhs, rhs -> lhs + rhs },
                InfixEvaluatableOperator(CharInParser('-', name = "-"), 40) { lhs, rhs -> lhs - rhs },
                InfixEvaluatableOperator(CharInParser('*', name = "*"), 60) { lhs, rhs -> lhs * rhs },
                InfixEvaluatableOperator(CharInParser('/', name = "/"), 60) { lhs, rhs -> lhs / rhs },

                PrefixEvaluatableOperator(CharInParser('-', name = "-"), 80) { rhs -> -rhs },
                InfixrEvaluatableOperator(CharInParser('^', name = "^"), 70) { lhs, rhs -> lhs.pow(rhs) }
        )

        val parser = ExpressionParser(DigitParser(name = "val"), operators)

        val visitor = ExpressionVisitor(operators) { it.text.toDouble() }

        val inputs = listOf(
                "2 ^ 3" to (2.0.pow(3)),
                "2 ^ 3 ^ 4" to (2.0.pow(3.0.pow(4))),

                "1" to (1.0),
                "1 + 2" to (1.0 + 2),
                "1 + 2 + 3 + 4" to (1.0 + 2 + 3 + 4),
                "1 - 2 - 3 - 4" to (1.0 - 2 - 3 - 4),
                "1 + 2 - 3 + 4" to (1.0 + 2 - 3 + 4),
                "1 - 2 + 3 - 4" to (1.0 - 2 + 3 - 4),

                "1" to (1.0),
                "1 * 2" to (1.0 * 2),
                "1 * 2 * 3 * 4" to (1.0 * 2 * 3 * 4),
                "1 / 2 / 3 / 4" to (1.0 / 2 / 3 / 4),
                "1 * 2 / 3 * 4" to (1.0 * 2 / 3 * 4),
                "1 / 2 * 3 / 4" to (1.0 / 2 * 3 / 4),

                "-1" to (-1.0),
                "--1" to (-(-1.0)),
                "---1" to (-(-(-1.0))),

                "-1 + 2" to (-1.0 + 2),
                "-1 - 2" to (-1.0 - 2),
                "-1 * 2" to (-1.0 * 2),
                "-1 / 2" to (-1.0 / 2),
                "-2 ^ 3" to ((-2.0).pow(3)),

                "1 + -2" to (1.0 + -2),
                "1 - -2" to (1.0 - -2),
                "1 * -2" to (1.0 * -2),
                "1 / -2" to (1.0 / -2),
                "2 ^ -3" to (2.0.pow(-3)),

                "-1 + -2" to (-1.0 + -2),
                "-1 - -2" to (-1.0 - -2),
                "-1 * -2" to (-1.0 * -2),
                "-1 / -2" to (-1.0 / -2),
                "-2 ^ -3" to ((-2.0).pow(-3)),

                "1 + 2 * 3" to (1.0 + 2 * 3),
                "2 ^ 3 ^ 4 ^ 5 * 6" to ((2.0.pow(3.0.pow(4.0.pow(5)))) * 6)
        )

        return inputs.map { input ->
            DynamicTest.dynamicTest("parse [${input.first}]") {
                val output = parser.test(input.first, skipWhitespace = true)

                val context = ExpressionContext<Double>()

                expectThat(output)
                        .parsedCorrectly()
                        .node()
                        .isNotNull()
                        .get { visit(context, visitor).value }
                        .isEqualTo(input.second)
            }
        }
    }
}
