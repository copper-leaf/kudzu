package com.copperleaf.kudzu

import com.copperleaf.kudzu.parser.ParserContext
import com.copperleaf.kudzu.parser.chars.CharInParser
import com.copperleaf.kudzu.parser.chars.DigitParser
import com.copperleaf.kudzu.parser.choice.PredictiveChoiceParser
import com.copperleaf.kudzu.parser.expression.ExpressionParser
import com.copperleaf.kudzu.parser.expression.Operator
import com.copperleaf.kudzu.parser.many.AtLeastParser
import com.copperleaf.kudzu.parser.many.ManyParser
import com.copperleaf.kudzu.parser.mapped.MappedParser
import com.copperleaf.kudzu.parser.maybe.MaybeParser
import com.copperleaf.kudzu.parser.sequence.SequenceParser
import com.copperleaf.kudzu.parser.text.IdentifierTokenParser
import com.copperleaf.kudzu.parser.text.ScanParser
import com.copperleaf.kudzu.parser.value.IntLiteralParser
import kotlin.math.pow
import kotlin.test.Test

@Suppress("UNUSED_VARIABLE", "UNUSED_DESTRUCTURED_PARAMETER_ENTRY")
class ReadmeTests {

    @Test
    fun test1() {
        val intLiteralParser = MappedParser(
            SequenceParser(
                MaybeParser(
                    CharInParser('-')
                ),
                AtLeastParser(
                    DigitParser(),
                    minSize = 1
                )
            )
        ) { it.text.toInt() }

        val (node, remainingText) = intLiteralParser.parse(ParserContext.fromString("-123"))
        val parsedValue: Int = node.value
        expectThat(parsedValue).isEqualTo(-123)
    }

    @Test
    fun test2() {
        val variableMap = mapOf(
            "asdf" to 1,
            "qwerty" to 2,
        )

        val patternToReplace = MappedParser(
            SequenceParser(
                CharInParser('#'),
                CharInParser('{'),
                IdentifierTokenParser(),
                CharInParser('}'),
            )
        ) { (nodeContext, _, _, identifier, _) ->
            variableMap[identifier.text]
        }

        val findAndReplaceParser = ManyParser(
            PredictiveChoiceParser(
                patternToReplace,
                ScanParser(patternToReplace),
            )
        )

        val (node, remainingText) = findAndReplaceParser.parse(
            ParserContext.fromString(
                "the value of #{asdf} is 1, but #{qwerty} is 2"
            )
        )
        expectThat(node.text).isEqualTo("the value of 1 is 1, but 2 is 2")
    }

    @Test
    fun test3() {
        val expressionParser = ExpressionParser<Int>(
            termParser = { IntLiteralParser() },

            Operator.Infix(op = "+", 40) { l, r -> l + r },
            Operator.Infix(op = "-", 40) { l, r -> l - r },
            Operator.Infix(op = "*", 60) { l, r -> l * r },
            Operator.Infix(op = "/", 60) { l, r -> l / r },

            Operator.Prefix(op = "-", 80) { r -> -r },
            Operator.Infixr(op = "^", 70) { l, r -> l.toDouble().pow(r).toInt() },
        )

        val (node, remainingText) = expressionParser.parse(
            ParserContext.fromString(
                "2 ^ ((4 - 2) * 2)",
                skipWhitespace = true
            )
        )
        val value = expressionParser.evaluator.evaluate(node)
        expectThat(value).isEqualTo(16)
    }
}
