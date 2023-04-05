package com.copperleaf.kudzu.parser.expression

import com.copperleaf.kudzu.expectThat
import com.copperleaf.kudzu.isEqualTo
import com.copperleaf.kudzu.isNotNull
import com.copperleaf.kudzu.node
import com.copperleaf.kudzu.node.mapped.ValueNode
import com.copperleaf.kudzu.parsedCorrectly
import com.copperleaf.kudzu.parsedIncorrectly
import com.copperleaf.kudzu.parser.Parser
import com.copperleaf.kudzu.parser.mapped.MappedParser
import com.copperleaf.kudzu.parser.value.BooleanLiteralParser
import com.copperleaf.kudzu.parser.value.DoubleLiteralParser
import com.copperleaf.kudzu.parser.value.IntLiteralParser
import com.copperleaf.kudzu.test
import io.kotest.core.spec.style.StringSpec
import kotlin.math.pow

class TestExpression : StringSpec({

    "testDoubleExpressionParser" {
        val parser = ExpressionParser<Double>(
            termParser = { IntAsDoubleParser() },

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
                    val kudzuExpressionResult: Double = parser.evaluator.evaluate(it)
                    val kotlinExpressionResult = input.second()

                    kudzuExpressionResult.isEqualTo(kotlinExpressionResult)
                }
        }
    }

    "testBooleanExpressionParser" {
        val parser = ExpressionParser<Boolean>(
            termParser = { BooleanLiteralParser() },

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
                    val kudzuExpressionResult: Boolean = parser.evaluator.evaluate(it)
                    val kotlinExpressionResult = input.second()

                    kudzuExpressionResult.isEqualTo(kotlinExpressionResult)
                }
        }
    }

// Parentheses support
// ---------------------------------------------------------------------------------------------------------------------

    "testParenthesizedExpression" {
        val parser = ExpressionParser<Double>(
            termParser = { IntAsDoubleParser() },
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
            " ( ( 1 + 2 ) * 3 ) " to { (1.0 + 2.0) * 3.0 },
        )

        inputs.map { input ->
            val output = parser.test(input.first, skipWhitespace = true)

            expectThat(output)
                .parsedCorrectly()
                .node()
                .isNotNull()
                .also {
                    val kudzuExpressionResult: Double = parser.evaluator.evaluate(it)
                    val kotlinExpressionResult = input.second()

                    kudzuExpressionResult.isEqualTo(kotlinExpressionResult)
                }
        }
    }

    "testNonParenthesizedExpression" {
        val parser = ExpressionParser<Double>(
            termParser = { IntAsDoubleParser() },
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

            if (input.second != null) {
                expectThat(output)
                    .parsedCorrectly()
                    .node()
                    .isNotNull()
                    .also {
                        val kudzuExpressionResult: Double = parser.evaluator.evaluate(it)
                        val kotlinExpressionResult = input.second!!()

                        kudzuExpressionResult.isEqualTo(kotlinExpressionResult)
                    }
            } else {
                expectThat(output)
                    .parsedIncorrectly()
            }
        }
    }

// AST simplification support
// ---------------------------------------------------------------------------------------------------------------------

    "testSimplifiedExpression" {
        val parser = ExpressionParser<Double>(
            termParser = { DoubleLiteralParser() },
            parenthesizedTerm = true,
            simplifyAst = true,
            operators = listOf(
                Operator.Infix(op = "+", 40) { l, r -> l + r },
                Operator.Infix(op = "-", 40) { l, r -> l - r },
                Operator.Infix(op = "*", 60) { l, r -> l * r },
                Operator.Infix(op = "/", 60) { l, r -> l / r },

                Operator.Prefix(op = "-", 80) { r -> -r },
                Operator.Infixr(op = "^", 70) { l, r -> l.pow(r) },
            )
        )
        val output = parser.test("(1.1 + 2.2) * 3.3", skipWhitespace = true)

        expectThat(output)
            .parsedCorrectly(
                """
                |(RootExpressionNode:
                |  (InfixOperatorNode:
                |    (RootExpressionNode:
                |      (InfixOperatorNode:
                |        (ValueNode: '1.1')
                |        (BinaryOperationNode:
                |          (TextNode: '+')
                |          (ValueNode: '2.2')
                |        )
                |      )
                |    )
                |    (BinaryOperationNode:
                |      (TextNode: '*')
                |      (ValueNode: '3.3')
                |    )
                |  )
                |)
                """.trimMargin()
            )
    }

    "testNonSimplifiedExpression" {
        val parser = ExpressionParser<Double>(
            termParser = { DoubleLiteralParser() },
            parenthesizedTerm = true,
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
        val output = parser.test("(1.1 + 2.2) * 3.3", skipWhitespace = true)

        expectThat(output)
            .parsedCorrectly(
                """
                |(RootExpressionNode:
                |  (InfixOperatorNode:
                |    (InfixOperatorNode:
                |      (InfixrOperatorNode:
                |        (PrefixOperatorNode:
                |          (RootExpressionNode:
                |            (InfixOperatorNode:
                |              (InfixOperatorNode:
                |                (InfixrOperatorNode:
                |                  (PrefixOperatorNode:
                |                    (ValueNode: '1.1')
                |                  )
                |                )
                |              )
                |              (BinaryOperationNode:
                |                (TextNode: '+')
                |                (InfixOperatorNode:
                |                  (InfixrOperatorNode:
                |                    (PrefixOperatorNode:
                |                      (ValueNode: '2.2')
                |                    )
                |                  )
                |                )
                |              )
                |            )
                |          )
                |        )
                |      )
                |      (BinaryOperationNode:
                |        (TextNode: '*')
                |        (InfixrOperatorNode:
                |          (PrefixOperatorNode:
                |            (ValueNode: '3.3')
                |          )
                |        )
                |      )
                |    )
                |  )
                |)
                """.trimMargin()
            )
    }

// Operator alias support
// ---------------------------------------------------------------------------------------------------------------------

    "testOperatorAliases" {
        val parser = ExpressionParser<Double>(
            termParser = { IntAsDoubleParser() },

            Operator.Infix(op = "+", 40, aliases = listOf("plus", "pp")) { l, r -> l + r },
            Operator.Infix(op = "-", 40, aliases = listOf("minus", "mm")) { l, r -> l - r },
            Operator.Infix(op = "*", 60, aliases = listOf("times", "tt")) { l, r -> l * r },
            Operator.Infix(op = "/", 60, aliases = listOf("dividedBy", "dd")) { l, r -> l / r },

            Operator.Prefix(op = "-", 80, aliases = listOf("negative")) { r -> -r },
            Operator.Infixr(op = "^", 70, aliases = listOf("raisedToThePowerOf")) { l, r -> l.pow(r) },
        )

        val inputs = listOf(
            "1 + 2 * 3 - 4 / -5 ^ 6" to { 1.0 + 2.0 * 3.0 - 4.0 / (-5.0).pow(6.0) },
            "1 plus 2 times 3 minus 4 dividedBy negative 5 raisedToThePowerOf 6" to {
                1.0 + 2.0 * 3.0 - 4.0 / (-5.0).pow(6.0)
            },
            "1 + 2 times 3 - 4 dividedBy negative 5 ^ 6" to { 1.0 + 2.0 * 3.0 - 4.0 / (-5.0).pow(6.0) },
        )

        inputs.map { input ->
            val output = parser.test(input.first, skipWhitespace = true, logErrors = true)

            expectThat(output)
                .parsedCorrectly()
                .node()
                .isNotNull()
                .also {
                    val kudzuExpressionResult: Double = parser.evaluator.evaluate(it)
                    val kotlinExpressionResult = input.second()

                    kudzuExpressionResult.isEqualTo(kotlinExpressionResult)
                }
        }
    }
})

class IntAsDoubleParser(
    private val parser: Parser<ValueNode<Double>> = MappedParser(IntLiteralParser()) { it.value.toDouble() }
) : Parser<ValueNode<Double>> by parser
