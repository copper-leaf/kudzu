package com.copperleaf.kudzu.parser

import com.copperleaf.kudzu.Parser
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

    @Test
    fun testParserWithNameDoesNotThrow() {
        expectThat(catching {
            InfixEvaluableOperator<ExpressionContext<Double>, Double>(
                CharInParser('+', name = "+"),
                40
            ) { _, lhs, rhs -> lhs + rhs }
        })
            .isNull()
    }

    @Test
    fun testParserWithoutNameThrows() {
        expectThat(catching {
            InfixEvaluableOperator<ExpressionContext<Double>, Double>(
                CharInParser('+'),
                40
            ) { _, lhs, rhs -> lhs + rhs }
        })
            .isNotNull()
            .get { message }
            .isEqualTo("Operator parser must have a name!")
    }

    @Test
    fun testCreatingParserWithUniqueOperatorNamesDoesNotThrow() {
        expectThat(catching {
            val operators = listOf<EvaluableOperator<ExpressionContext<Double>, Double>>(
                InfixEvaluableOperator(CharInParser('+', name = "plus"), 40) { _, lhs, rhs -> lhs + rhs },
                InfixEvaluableOperator(CharInParser('-', name = "minus"), 40) { _, lhs, rhs -> lhs - rhs }
            )
            ExpressionParser(DigitParser(name = "val"), operators)
        })
            .isNull()
    }

    @Test
    fun testCreatingParserWithDuplicatedOperatorNamesThrows() {
        expectThat(catching {
            val operators = listOf<EvaluableOperator<ExpressionContext<Double>, Double>>(
                InfixEvaluableOperator(CharInParser('+', name = "op"), 40) { _, lhs, rhs -> lhs + rhs },
                InfixEvaluableOperator(CharInParser('-', name = "op"), 40) { _, lhs, rhs -> lhs - rhs }
            )
            ExpressionParser(DigitParser(name = "val"), operators)
        })
            .isNotNull()
            .get { message }
            .isEqualTo(
                "All operators must have unique names!\n" +
                        "non-unique operator counts: ['op' -> 2]"
            )
    }

// Test Numeric Expressions
//----------------------------------------------------------------------------------------------------------------------

    @TestFactory
    fun testNumericExpressionParser(): List<DynamicTest> {
        val operators = listOf<EvaluableOperator<ExpressionContext<Double>, Double>>(
            InfixEvaluableOperator(CharInParser('+', name = "plus"), 40) { _, lhs, rhs -> lhs + rhs },
            InfixEvaluableOperator(CharInParser('-', name = "minus"), 40) { _, lhs, rhs -> lhs - rhs },
            InfixEvaluableOperator(CharInParser('*', name = "mul"), 60) { _, lhs, rhs -> lhs * rhs },
            InfixEvaluableOperator(CharInParser('/', name = "div"), 60) { _, lhs, rhs -> lhs / rhs },

            PrefixEvaluableOperator(CharInParser('-', name = "uminus"), 80) { _, rhs -> -rhs },
            InfixrEvaluableOperator(CharInParser('^', name = "exp"), 70) { _, lhs, rhs -> lhs.pow(rhs) }
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

    @TestFactory
    fun testBooleanExpressionParser(): List<DynamicTest> {
        val operators = listOf<EvaluableOperator<ExpressionContext<Any>, Any>>(
            InfixEvaluableOperator(
                WordParser("&&", name = "and"),
                40
            ) { _, lhs, rhs -> lhs.toString().toBoolean() && rhs.toString().toBoolean() },
            InfixEvaluableOperator(
                WordParser("||", name = "or"),
                30
            ) { _, lhs, rhs -> lhs.toString().toBoolean() || rhs.toString().toBoolean() },
            PrefixEvaluableOperator(
                CharInParser('!', name = "not"),
                130
            ) { _, rhs -> !(rhs.toString().toBoolean()) },

            InfixEvaluableOperator(
                WordParser(">=", name = "gte"),
                90
            ) { _, lhs, rhs -> lhs.toString().toInt() >= rhs.toString().toInt() },
            InfixEvaluableOperator(
                WordParser(">", name = "gt"),
                90
            ) { _, lhs, rhs -> lhs.toString().toInt() > rhs.toString().toInt() },
            InfixEvaluableOperator(
                WordParser("<=", name = "lte"),
                80
            ) { _, lhs, rhs -> lhs.toString().toInt() <= rhs.toString().toInt() },
            InfixEvaluableOperator(
                WordParser("<", name = "lt"),
                80
            ) { _, lhs, rhs -> lhs.toString().toInt() < rhs.toString().toInt() },
            InfixEvaluableOperator(
                WordParser("==", name = "eq"),
                80
            ) { _, lhs, rhs -> lhs.toString().toInt() == rhs.toString().toInt() }
        )

        val parser = ExpressionParser(TokenParser(name = "val"), operators)

        val visitor = ExpressionVisitor(operators) { _, node -> node.text }

        val inputs = listOf(
            "true" to (true),
            "false" to (false),
            "!true" to (!true),
            "!false" to (!false),
            "true && false" to (true && false),
            "true || false" to (true || false),
            "1 > 2" to (1 > 2),
            "1 >= 2" to (1 >= 2),
            "1 < 2" to (1 < 2),
            "1 <= 2" to (1 <= 2),
            "1 == 2" to (1 == 2),
            "1 > 2 && 3 <= 4" to (1 > 2 && 3 <= 4),
            "1 > 2 && false || 4 <= 4" to (1 > 2 && false || 4 <= 4)
        )

        return inputs.map { input ->
            DynamicTest.dynamicTest("parse [${input.first}]") {
                val output = parser.test(input.first, skipWhitespace = true)

                val context = ExpressionContext<Any>()

                expectThat(output)
                    .parsedCorrectly()
                    .node()
                    .isNotNull()
                    .get { visit(context, visitor).value.toString().toBoolean() }
                    .isEqualTo(input.second)
            }
        }
    }

    @TestFactory
    fun testBooleanExpressionParserWithoutSkippingWhitespace(): List<DynamicTest> {
        val operators = listOf<EvaluableOperator<ExpressionContext<Any>, Any>>(
            InfixEvaluableOperator(
                createOperatorParser("and", "&&"),
                40
            ) { _, lhs, rhs -> lhs.toString().toBoolean() && rhs.toString().toBoolean() },
            InfixEvaluableOperator(
                createOperatorParser("or", "||"),
                30
            ) { _, lhs, rhs -> lhs.toString().toBoolean() || rhs.toString().toBoolean() },
            PrefixEvaluableOperator(
                createOperatorParser("not", "!"),
                130
            ) { _, rhs -> !(rhs.toString().toBoolean()) },

            InfixEvaluableOperator(
                createOperatorParser("gte", ">="),
                90
            ) { _, lhs, rhs -> lhs.toString().toInt() >= rhs.toString().toInt() },
            InfixEvaluableOperator(
                createOperatorParser("gt", ">"),
                90
            ) { _, lhs, rhs -> lhs.toString().toInt() > rhs.toString().toInt() },
            InfixEvaluableOperator(
                createOperatorParser("lte", "<="),
                80
            ) { _, lhs, rhs -> lhs.toString().toInt() <= rhs.toString().toInt() },
            InfixEvaluableOperator(
                createOperatorParser("lt", "<"),
                80
            ) { _, lhs, rhs -> lhs.toString().toInt() < rhs.toString().toInt() },
            InfixEvaluableOperator(
                createOperatorParser("eq", "=="),
                80
            ) { _, lhs, rhs -> lhs.toString().toInt() == rhs.toString().toInt() }
        )

        val parser = ExpressionParser(
            SequenceParser(TokenParser(), OptionalWhitespaceParser(), name = "val"),
            operators
        )

        val visitor = ExpressionVisitor(operators) { _, node -> node.text }

        val inputs = listOf(
            "true" to (true),
            "false" to (false),
            "!true" to (!true),
            "!false" to (!false),
            "true && false" to (true && false),
            "true and false" to (true && false),
            "true || false" to (true || false),
            "true or false" to (true || false),
            "1 > 2" to (1 > 2),
            "1 gt 2" to (1 > 2),
            "1 >= 2" to (1 >= 2),
            "1 gte 2" to (1 >= 2),
            "1 < 2" to (1 < 2),
            "1 lt 2" to (1 < 2),
            "1 <= 2" to (1 <= 2),
            "1 lte 2" to (1 <= 2),
            "1 == 2" to (1 == 2),
            "1 eq 2" to (1 == 2),
            "1 > 2 && 3 <= 4" to (1 > 2 && 3 <= 4),
            "1 gt 2 and 3 lte 4" to (1 > 2 && 3 <= 4),
            "1 > 2 && false || 4 <= 4" to (1 > 2 && false || 4 <= 4),
            "1 gt 2 && false or 4 lte 4" to (1 > 2 && false || 4 <= 4)
        )

        return inputs.map { input ->
            DynamicTest.dynamicTest("parse [${input.first}]") {
                val output = parser.test(input.first)

                val context = ExpressionContext<Any>()

                expectThat(output)
                    .parsedCorrectly()
                    .node()
                    .isNotNull()
                    .get { visit(context, visitor).value.toString().toBoolean() }
                    .isEqualTo(input.second)
            }
        }
    }

}

private fun createOperatorParser(name: String, vararg tokens: String): Parser {
    return SequenceParser(
        ExactChoiceParser(*tokens.map { WordParser(it) }.toTypedArray(), WordParser(name)),
        OptionalWhitespaceParser(),
        name = name
    )
}