package com.copperleaf.kudzu.parser.expression

import com.copperleaf.kudzu.expectThat
import com.copperleaf.kudzu.isEqualTo
import com.copperleaf.kudzu.isNotNull
import com.copperleaf.kudzu.node
import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.node.expression.InfixOperatorNode
import com.copperleaf.kudzu.node.expression.InfixrOperatorNode
import com.copperleaf.kudzu.node.expression.PostfixOperatorNode
import com.copperleaf.kudzu.node.expression.PrefixOperatorNode
import com.copperleaf.kudzu.parsedCorrectly
import com.copperleaf.kudzu.parser.chars.CharInParser
import com.copperleaf.kudzu.parser.chars.DigitParser
import com.copperleaf.kudzu.test
import kotlin.math.pow
import kotlin.test.Test

@OptIn(ExperimentalStdlibApi::class)
class TestExpression {

    @Test
    fun testBasicScan() {
        val operators = listOf(
            Operator.Infix(op = CharInParser('+'), 40),
            Operator.Infix(op = CharInParser('-'), 40),
            Operator.Infix(op = CharInParser('*'), 60),
            Operator.Infix(op = CharInParser('/'), 60),

            Operator.Prefix(op = CharInParser('-'), 80),
            Operator.Infixr(op = CharInParser('^'), 70),
        )

        val parser = ExpressionParser(
            termParser = DigitParser(),
            operators = operators.toTypedArray()
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
            println("parse [${input.first}]").also {
                val output = parser.test(input.first, skipWhitespace = true)


                expectThat(output)
                    .parsedCorrectly()
                    .node()
                    .isNotNull()
                    .also {
                        val kudzuExpressionResult: Double = evaluateExpression(it)
                        val kotlinExpressionResult = input.second()

                        println("    -> kudzu: $kudzuExpressionResult, kotlin: $kotlinExpressionResult")

                        kudzuExpressionResult.isEqualTo(kotlinExpressionResult)
                    }
            }
        }
    }

    private fun evaluateExpression(node: Node): Double {
        return try {
            when (node) {
                is InfixOperatorNode -> node.evaluate()
                is InfixrOperatorNode -> node.evaluate()
                is PrefixOperatorNode -> node.evaluate()
                is PostfixOperatorNode -> node.evaluate()
                else -> node.text.toDouble()
            }
        }
        catch (e: Exception) {
                throw e
        }
    }

    private fun InfixOperatorNode.evaluate(): Double {
        var result = evaluateExpression(leftOperand)

        for (node in operationNodes) {
            val rightOperatorResult = evaluateExpression(node.operand)
            result = node.operator.text.applyBinary(result, rightOperatorResult)
        }

        return result
    }

    private fun InfixrOperatorNode.evaluate(): Double {
        var result = evaluateExpression(leftOperand)

        if(operation != null) {
            val rightOperationResult = evaluateExpression(operation!!.operand)
            result = operation!!.operator.text.applyBinary(result, rightOperationResult)
        }

        return result
    }

    private fun PrefixOperatorNode.evaluate(): Double {
        var result = evaluateExpression(operand)

        for (node in operatorNodes) {
            result = node.text.applyUnary(result)
        }

        return result
    }

    private fun PostfixOperatorNode.evaluate(): Double {
        TODO()
    }

    private fun String.applyUnary(node: Double): Double {
        return when (this) {
            "-" -> node * -1
            else -> error("unknown unary operator: $this")
        }
    }

    private fun String.applyBinary(left: Double, right: Double): Double {
        return when (this) {
            "+" -> left + right
            "-" -> left - right
            "*" -> left * right
            "/" -> left / right
            "^" -> left.pow(right)
            else -> error("unknown unary operator: $this")
        }
    }


}
