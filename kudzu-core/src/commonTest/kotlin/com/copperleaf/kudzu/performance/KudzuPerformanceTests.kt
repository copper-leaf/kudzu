package com.copperleaf.kudzu.performance

import com.copperleaf.kudzu.expectThat
import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.parsedCorrectly
import com.copperleaf.kudzu.parser.Parser
import com.copperleaf.kudzu.parser.chars.CharInParser
import com.copperleaf.kudzu.parser.chars.DigitParser
import com.copperleaf.kudzu.parser.choice.PredictiveChoiceParser
import com.copperleaf.kudzu.parser.lazy.LazyParser
import com.copperleaf.kudzu.parser.many.ManyParser
import com.copperleaf.kudzu.parser.maybe.MaybeParser
import com.copperleaf.kudzu.parser.sequence.SequenceParser
import com.copperleaf.kudzu.test
import kotlin.test.Test

class KudzuPerformanceTests {

    val underTest: Parser<Node>

    init {
        val Expression = LazyParser<Node>()
        val Sum = LazyParser<Node>()
        val Product = LazyParser<Node>()
        val Power = LazyParser<Node>()
        val Value = LazyParser<Node>()

        // Value production rule
        //     Value ::= [0-9]+ | '(' Exprssion ')'
        Value uses PredictiveChoiceParser(
            ManyParser(
                DigitParser()
            ),
            SequenceParser(
                CharInParser('('),
                Expression,
                CharInParser(')'),
            )
        )

        // Power production rule
        //     Power ::= Value ('^' Power)?
        Power uses SequenceParser(
            Value,
            MaybeParser(
                SequenceParser(
                    CharInParser('^'),
                    Power,
                )
            )
        )

        // Product production rule
        //     Product ::= Power (('*' | '/') Power)*
        Product uses SequenceParser(
            Power,
            MaybeParser(
                ManyParser(
                    SequenceParser(
                        CharInParser('*', '/'),
                        Power
                    )
                )
            )
        )

        // Sum production rule
        //     Sum ::= Product (('+' / '-') Product)*
        Sum uses SequenceParser(
            Product,
            MaybeParser(
                ManyParser(
                    SequenceParser(
                        CharInParser('+', '-'),
                        Product
                    )
                )
            )
        )

        // Expression production rule
        Expression uses Sum

        // testing the expression
        underTest = Expression
    }

    /*
    Last run data:

    Total duration of 1000 runs: 115ms
    Mean test duration: 115us
    Test duration spread: [44.1us, 108us, 7.17ms]
    standard deviation: 328.92861634591554us
     */
    @Test
    fun runPerformanceTestOnSimpleExpression() = performanceTest(10_000, 1000) {
        val test = underTest.test("1 - 2 * (3 + 4 / 5 ^ 6 * (7 - 8)) * 9", skipWhitespace = true, logErrors = true)

        expectThat(test).parsedCorrectly()
    }

//    @Test // this test takes a long time, so only run it as-needed and not part of normal development cycles
    fun runPerformanceTestOnDeepNestedExpression() {
        val depth = 100_000
        val expresssionStart = (1..depth).joinToString(separator = "") { "1 + (" }
        val expresssionEnd = (1..depth).joinToString(separator = "") { ")" }
        val expression = "$expresssionStart 1 + 1 $expresssionEnd"

        performanceTest(10, 5) {
            val test = underTest.test(expression, skipWhitespace = true, logErrors = true)
            expectThat(test).parsedCorrectly()
        }
    }
}
