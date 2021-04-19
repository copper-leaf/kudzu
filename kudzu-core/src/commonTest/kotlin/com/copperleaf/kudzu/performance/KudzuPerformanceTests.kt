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
        Value.parser = PredictiveChoiceParser(
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
        Power.parser = SequenceParser(
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
        Product.parser = SequenceParser(
            Power,
            ManyParser(
                SequenceParser(
                    CharInParser('*', '/'),
                    Power
                )
            )
        )

        // Sum production rule
        //     Sum ::= Product (('+' / '-') Product)*
        Sum.parser = SequenceParser(
            Product,
            ManyParser(
                SequenceParser(
                    CharInParser('+', '-'),
                    Product
                )
            )
        )

        // Expression production rule
        Expression.parser = Sum

        // testing the expression
        underTest = Expression
    }

    /*
    Last run data:

    Total duration of 1000 runs: 252ms
    Mean test duration: 252us
    Test duration spread: [157us, 166us, 9.31ms]
    standard deviation: 301.70829903783067us
     */
    @Test
    fun runPerformanceTest() = performanceTest(1000, 100) {
        val test = underTest.test("1 - 2 * (3 + 4 / 5 ^ 6 * (7 - 8)) * 9", skipWhitespace = true, logErrors = true)

        expectThat(test).parsedCorrectly()
    }
}
