package com.copperleaf.kudzu.parser.expression

import com.copperleaf.kudzu.expectThat
import com.copperleaf.kudzu.isEqualTo
import com.copperleaf.kudzu.isNotNull
import com.copperleaf.kudzu.node
import com.copperleaf.kudzu.parsedCorrectly
import com.copperleaf.kudzu.parsedIncorrectly
import com.copperleaf.kudzu.parser.chars.DigitParser
import com.copperleaf.kudzu.parser.choice.ExactChoiceParser
import com.copperleaf.kudzu.parser.mapped.MappedParser
import com.copperleaf.kudzu.parser.text.LiteralTokenParser
import com.copperleaf.kudzu.test
import kotlin.math.pow
import kotlin.test.Test

@OptIn(ExperimentalStdlibApi::class)
class TestExpression {

    @Test
    fun testDoubleExpressionParser() {
        val parser = ExpressionParser<Double>(
            termParser = MappedParser(DigitParser()) { it.text.toDouble() },

            Operator.Infix(op = "+", 40) { l, r -> l + r },
            Operator.Infix(op = "-", 40) { l, r -> l - r },
            Operator.Infix(op = "*", 60) { l, r -> l * r },
            Operator.Infix(op = "/", 60) { l, r -> l / r },

            Operator.Prefix(op = "-", 80) { r -> -r },
            Operator.Infixr(op = "^", 70) { l, r -> l.pow(r) },
        )

        val inputs = listOf(
            "2 ^ 3" to { 2.0.pow(3.0) },
            "1 + 2 * 3 - 4 / -5 ^ 6" to { 1.0 + 2.0 * 3.0 - 4.0 / (-5.0).pow(6.0) },
            "2 ^ 3" to { 2.0.pow(3) },
            "2 ^ 2 ^ 2" to { 2.0.pow(2.0.pow(2.0)) },

            "1" to { 1.0 },
            "1 + 2" to { 1.0 + 2 },
            "1 + 2 + 3 + 4" to { 1.0 + 2 + 3 + 4 },
            "1 - 2 - 3 - 4" to { 1.0 - 2 - 3 - 4 },
            "1 + 2 - 3 + 4" to { 1.0 + 2 - 3 + 4 },
            "1 - 2 + 3 - 4" to { 1.0 - 2 + 3 - 4 },

            "1" to { 1.0 },
            "1 * 2" to { 1.0 * 2 },
            "1 * 2 * 3 * 4" to { 1.0 * 2 * 3 * 4 },
            "1 / 2 / 3 / 4" to { 1.0 / 2 / 3 / 4 },
            "1 * 2 / 3 * 4" to { 1.0 * 2 / 3 * 4 },
            "1 / 2 * 3 / 4" to { 1.0 / 2 * 3 / 4 },

            "-1" to { -1.0 },
            "--1" to { -(-1.0) },
            "---1" to { -(-(-1.0)) },

            "-1 + 2" to { -1.0 + 2 },
            "-1 - 2" to { -1.0 - 2 },
            "-1 * 2" to { -1.0 * 2 },
            "-1 / 2" to { -1.0 / 2 },
            "-2 ^ 3" to { (-2.0).pow(3) },

            "1 + -2" to { 1.0 + -2 },
            "1 - -2" to { 1.0 - -2 },
            "1 * -2" to { 1.0 * -2 },
            "1 / -2" to { 1.0 / -2 },
            "2 ^ -3" to { 2.0.pow(-3) },

            "-1 + -2" to { -1.0 + -2 },
            "-1 - -2" to { -1.0 - -2 },
            "-1 * -2" to { -1.0 * -2 },
            "-1 / -2" to { -1.0 / -2 },
            "-2 ^ -3" to { (-2.0).pow(-3) },

            "1 + 2 * 3" to { 1.0 + 2 * 3 },
            "2 ^ 3 ^ 4 ^ 5 * 6" to { (2.0.pow(3.0.pow(4.0.pow(5)))) * 6 }
        )

        inputs.map { input ->
            val output = parser.test(input.first, skipWhitespace = true)

            expectThat(output)
                .parsedCorrectly()
                .node()
                .isNotNull()
                .also {
                    val kudzuExpressionResult: Double = parser.evaluator.evaluateExpression(it)
                    val kotlinExpressionResult = input.second()

                    kudzuExpressionResult.isEqualTo(kotlinExpressionResult)
                }
        }
    }

    @Test
    fun testBooleanExpressionParser() {
        val parser = ExpressionParser<Boolean>(
            termParser = MappedParser(
                ExactChoiceParser(
                    LiteralTokenParser("true"),
                    LiteralTokenParser("false"),
                )
            ) { it.text.toBoolean() },

            Operator.Infix("&&", 40) { l, r -> l && r },
            Operator.Infix("||", 30) { l, r -> l || r },
            Operator.Prefix("!", 130) { r -> !r },

            Operator.Infix(">=", 90) { l, r -> l >= r },
            Operator.Infix(">", 90) { l, r -> l > r },
            Operator.Infix("<=", 80) { l, r -> l <= r },
            Operator.Infix("<", 80) { l, r -> l < r },
            Operator.Infix("==", 80) { l, r -> l == r },
        )

        val inputs = listOf(
            "true" to { true },
            "false" to { false },
            "!true" to { !true },
            "!false" to { !false },
            "true && false" to { true && false },
            "true || false" to { true || false },
//            "1 > 2" to { 1 > 2 },
//            "1 >= 2" to { 1 >= 2 },
//            "1 < 2" to { 1 < 2 },
//            "1 <= 2" to { 1 <= 2 },
//            "1 == 2" to { 1 == 2 },
//            "1 > 2 && 3 <= 4" to { 1 > 2 && 3 <= 4 },
//            "1 > 2 && false || 4 <= 4" to { 1 > 2 && false || 4 <= 4 },
        )

        inputs.map { input ->
            val output = parser.test(input.first, skipWhitespace = true)

            expectThat(output)
                .parsedCorrectly()
                .node()
                .isNotNull()
                .also {
                    val kudzuExpressionResult: Boolean = parser.evaluator.evaluateExpression(it)
                    val kotlinExpressionResult = input.second()

                    kudzuExpressionResult.isEqualTo(kotlinExpressionResult)
                }
        }
    }

    @Test
    fun testParenthesizedExpression() {
        val parser = ExpressionParser<Double>(
            termParser = MappedParser(DigitParser()) { it.text.toDouble() },
            parenthesizedTerm = true,
            operators = listOf(
                Operator.Infix(op = "+", 40) { l, r -> l + r },
                Operator.Infix(op = "-", 40) { l, r -> l - r },
                Operator.Infix(op = "*", 60) { l, r -> l * r },
                Operator.Infix(op = "/", 60) { l, r -> l / r },

                Operator.Prefix(op = "-", 80) { r -> -r },
                Operator.Infixr(op = "^", 70) { l, r -> l.pow(r) },
            )
        )

        val inputs = listOf(
            "1 + 2 * 3" to { 1.0 + 2.0 * 3.0 },
            "1 + (2 * 3)" to { 1.0 + (2.0 * 3.0) },
            "(1 + 2) * 3" to { (1.0 + 2.0) * 3.0 },
        )

        inputs.map { input ->
            val output = parser.test(input.first, skipWhitespace = true)

            expectThat(output)
                .parsedCorrectly()
                .node()
                .isNotNull()
                .also {
                    val kudzuExpressionResult: Double = parser.evaluator.evaluateExpression(it)
                    val kotlinExpressionResult = input.second()

                    kudzuExpressionResult.isEqualTo(kotlinExpressionResult)
                }
        }
    }

    @Test
    fun testNonParenthesizedExpression() {
        val parser = ExpressionParser<Double>(
            termParser = MappedParser(DigitParser()) { it.text.toDouble() },
            parenthesizedTerm = false,
            operators = listOf(
                Operator.Infix(op = "+", 40) { l, r -> l + r },
                Operator.Infix(op = "-", 40) { l, r -> l - r },
                Operator.Infix(op = "*", 60) { l, r -> l * r },
                Operator.Infix(op = "/", 60) { l, r -> l / r },

                Operator.Prefix(op = "-", 80) { r -> -r },
                Operator.Infixr(op = "^", 70) { l, r -> l.pow(r) },
            )
        )

        val inputs = listOf(
            "1 + 2 * 3" to { 1.0 + 2.0 * 3.0 },
            "1 + (2 * 3)" to null,
            "(1 + 2) * 3" to null,
        )

        inputs.map { input ->
            val output = parser.test(input.first, skipWhitespace = true)

            println(output?.first)

            if(input.second != null) {
                expectThat(output)
                    .parsedCorrectly()
                    .node()
                    .isNotNull()
                    .also {
                        val kudzuExpressionResult: Double = parser.evaluator.evaluateExpression(it)
                        val kotlinExpressionResult = input.second!!()

                        kudzuExpressionResult.isEqualTo(kotlinExpressionResult)
                    }
            }
            else {
                expectThat(output)
                    .parsedIncorrectly()
            }
        }
    }
}
