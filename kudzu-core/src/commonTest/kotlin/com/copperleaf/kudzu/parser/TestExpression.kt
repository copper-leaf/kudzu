package com.copperleaf.kudzu.parser

import com.copperleaf.kudzu.*
import kotlin.math.pow
import kotlin.test.Test

class TestExpression {

    @Test
    fun testParserWithNameDoesNotThrow() {
        expectCatching {
            InfixEvaluableOperator<ExpressionContext<Double>, Double>(
                CharInParser('+', name = "+"),
                40
            ) { _, lhs, rhs -> lhs + rhs }
        }
            .isSuccess()
    }

    @Test
    fun testParserWithoutNameThrows() {
        expectCatching {
            InfixEvaluableOperator<ExpressionContext<Double>, Double>(
                CharInParser('+'),
                40
            ) { _, lhs, rhs -> lhs + rhs }
        }
            .isFailure()
            .get { message }
            .isEqualTo("Operator parser must have a name!")
    }

    @Test
    fun testCreatingParserWithUniqueOperatorNamesDoesNotThrow() {
        expectCatching {
            val operators = listOf<EvaluableOperator<ExpressionContext<Double>, Double>>(
                InfixEvaluableOperator(CharInParser('+', name = "plus"), 40) { _, lhs, rhs -> lhs + rhs },
                InfixEvaluableOperator(CharInParser('-', name = "minus"), 40) { _, lhs, rhs -> lhs - rhs }
            )
            ExpressionParser(DigitParser(name = "val"), operators)
        }
            .isSuccess()
    }

    @Test
    fun testCreatingParserWithDuplicatedOperatorNamesThrows() {
        expectCatching {
            val operators = listOf<EvaluableOperator<ExpressionContext<Double>, Double>>(
                InfixEvaluableOperator(CharInParser('+', name = "op"), 40) { _, lhs, rhs -> lhs + rhs },
                InfixEvaluableOperator(CharInParser('-', name = "op"), 40) { _, lhs, rhs -> lhs - rhs }
            )
            ExpressionParser(DigitParser(name = "val"), operators)
        }
            .isFailure()
            .get { message }
            .isEqualTo(
                "All operators must have unique names!\n" +
                    "non-unique operator counts: ['op' -> 2]"
            )
    }

// Test Numeric Expressions
// ----------------------------------------------------------------------------------------------------------------------

    @Test
    fun testNumericExpressionParser() {
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

        inputs.map { input ->
            println("parse [${input.first}]").also {
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
    fun testBooleanExpressionParser() {
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

        inputs.map { input ->
            println("parse [${input.first}]").also {
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

    @Test
    fun testBooleanExpressionParserWithoutSkippingWhitespace() {
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

        val visitor = ExpressionVisitor(operators) { _, node ->
            node.text
        }

        val inputs = listOf(
            "true" to (true),
//            "false" to (false),
//            "!true" to (!true),
//            "!false" to (!false),
//            "true && false" to (true && false),
//            "true and false" to (true && false),
//            "true || false" to (true || false),
//            "true or false" to (true || false),
//            "1 > 2" to (1 > 2),
//            "1 gt 2" to (1 > 2),
//            "1 >= 2" to (1 >= 2),
//            "1 gte 2" to (1 >= 2),
//            "1 < 2" to (1 < 2),
//            "1 lt 2" to (1 < 2),
//            "1 <= 2" to (1 <= 2),
//            "1 lte 2" to (1 <= 2),
//            "1 == 2" to (1 == 2),
//            "1 eq 2" to (1 == 2),
//            "1 > 2 && 3 <= 4" to (1 > 2 && 3 <= 4),
//            "1 gt 2 and 3 lte 4" to (1 > 2 && 3 <= 4),
//            "1 > 2 && false || 4 <= 4" to (1 > 2 && false || 4 <= 4),
//            "1 gt 2 && false or 4 lte 4" to (1 > 2 && false || 4 <= 4),
//
//            "true || 1 > 2" to (true || 1 > 2),
//            "true || true" to (true || true),
//            "1 > 2 || true" to (1 > 2 || true),
//            "false || 1 > 2 || true" to (false || 1 > 2 || true)
        )

        inputs.map { input ->
            println("parse [${input.first}]").also {
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

private fun createOperatorParser(name: String, vararg tokens: String): Parser<*> {
    return SequenceParser(
        ExactChoiceParser(*tokens.map { WordParser(it) }.toTypedArray(), WordParser(name)),
        OptionalWhitespaceParser(),
        name = name
    )
}
