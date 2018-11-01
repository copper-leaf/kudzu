package tests

import com.copperleaf.kudzu.parser.CharInParser
import com.copperleaf.kudzu.parser.CharParser
import com.copperleaf.kudzu.parser.DigitParser
import com.copperleaf.kudzu.parser.EvaluatableOperator
import com.copperleaf.kudzu.parser.ExpressionContext
import com.copperleaf.kudzu.parser.ExpressionParser
import com.copperleaf.kudzu.parser.ExpressionVisitor
import com.copperleaf.kudzu.parser.InfixEvaluatableOperator
import com.copperleaf.kudzu.parser.InfixrEvaluatableOperator
import com.copperleaf.kudzu.parser.PrefixEvaluatableOperator
import com.copperleaf.kudzu.visit
import kotlin.math.pow
import kotlin.test.Test
import kotlin.test.assertEquals

class TestLinesPresenter {

    @Test
    fun testExpressionParser() {
        assertEquals("(CharNode: 'a')", CharParser().test("a")!!.first.toString())


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

        return inputs.forEach { input ->
            println("TEST [${input.first}]")

            val output = parser.test(input.first, skipWhitespace = true)
            val context = ExpressionContext<Double>()
            val result = output!!.first.visit(context, visitor).value

            assertEquals(input.second, result)

            println("TEST [${input.first}] successful")
        }

    }

}