package com.copperleaf.kudzu.parser

import com.copperleaf.kudzu.node
import com.copperleaf.kudzu.parsedCorrectly
import com.copperleaf.kudzu.visit
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import strikt.api.catching
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isNotNull
import strikt.assertions.isNull
import kotlin.math.pow

class TestExpression {

    @TestFactory
    fun testExpressionParser(): List<DynamicTest> {
        val operators = listOf<EvaluableOperator<Double>>(
                InfixEvaluableOperator(CharInParser('+', name = "plus"), 40) { lhs, rhs -> lhs + rhs },
                InfixEvaluableOperator(CharInParser('-', name = "minus"), 40) { lhs, rhs -> lhs - rhs },
                InfixEvaluableOperator(CharInParser('*', name = "mul"), 60) { lhs, rhs -> lhs * rhs },
                InfixEvaluableOperator(CharInParser('/', name = "div"), 60) { lhs, rhs -> lhs / rhs },

                PrefixEvaluableOperator(CharInParser('-', name = "uminus"), 80) { rhs -> -rhs },
                InfixrEvaluableOperator(CharInParser('^', name = "exp"), 70) { lhs, rhs -> lhs.pow(rhs) }
        )

        val parser = ExpressionParser(DigitParser(name = "val"), operators)

        val visitor = ExpressionVisitor(operators) { _, node -> node.text.toDouble() }

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

    @Test
    fun testParserWithNameDoesNotThrow() {
        expectThat(catching { InfixEvaluableOperator<Double>(CharInParser('+', name = "+"), 40) { lhs, rhs -> lhs + rhs } })
            .isNull()
    }

    @Test
    fun testParserWithoutNameThrows() {
        expectThat(catching { InfixEvaluableOperator<Double>(CharInParser('+'), 40) { lhs, rhs -> lhs + rhs } })
            .isNotNull()
            .get { message }
            .isEqualTo("Operator parser must have a name!")
    }

    @Test
    fun testCreatingParserWithUniqueOperatorNamesDoesNotThrow() {
        expectThat(catching {
            val operators = listOf<EvaluableOperator<Double>>(
                InfixEvaluableOperator(CharInParser('+', name = "plus"), 40) { lhs, rhs -> lhs + rhs },
                InfixEvaluableOperator(CharInParser('-', name = "minus"), 40) { lhs, rhs -> lhs - rhs }
            )
            ExpressionParser(DigitParser(name = "val"), operators)
        })
            .isNull()
    }

    @Test
    fun testCreatingParserWithDuplicatedOperatorNamesThrows() {
        expectThat(catching {
            val operators = listOf<EvaluableOperator<Double>>(
                InfixEvaluableOperator(CharInParser('+', name = "op"), 40) { lhs, rhs -> lhs + rhs },
                InfixEvaluableOperator(CharInParser('-', name = "op"), 40) { lhs, rhs -> lhs - rhs }
            )
            ExpressionParser(DigitParser(name = "val"), operators)
        })
            .isNotNull()
            .get { message }
            .isEqualTo("All operators must have unique names!\n" +
                    "non-unique operator counts: ['op' -> 2]")
    }
}
