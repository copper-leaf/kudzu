package com.copperleaf.kudzu.integration

import com.copperleaf.kudzu.*
import com.copperleaf.kudzu.parser.AtLeastParser
import com.copperleaf.kudzu.parser.CharInParser
import com.copperleaf.kudzu.parser.CharNotInParser
import com.copperleaf.kudzu.parser.ChoiceParser
import com.copperleaf.kudzu.parser.DigitParser
import com.copperleaf.kudzu.parser.LazyParser
import com.copperleaf.kudzu.parser.ManyParser
import com.copperleaf.kudzu.parser.MaybeParser
import com.copperleaf.kudzu.parser.SequenceParser
import kotlin.test.Test

class TestModeratelyComplexGrammars {

    @Test
    fun testClogParser() {
        var input: String
        var output: Pair<Node, ParserContext>?
        val underTest = getClogParser()

        input = "asdf, asdf {this is my message} asdfasdfasdf"
        output = underTest.test(input)
        expectThat(output).parsedCorrectly()
    }

    @Test
    fun testArithmeticParser() {
        var input: String
        var output: Pair<Node, ParserContext>?
        val underTest = getArithmeticParser()

        input = "1 + (-2 * 3 * 4 * 5 * (6 / 7) - (8 / 9))"
        output = underTest.test(input, true)
        expectThat(output).parsedCorrectly()
    }

    @Test
    fun testClogExpressionParser() {
        var input: String
        var output: Pair<Node, ParserContext>?
        val underTest = getClogExpressionParser()

        input = "you have {1+(-2*3)} messages"
        output = underTest.test(input, true)
        expectThat(output).parsedCorrectly()
    }

    /*
     * Code island parser
     */
    private fun getClogParser(): Parser {
        val rawParser = AtLeastParser(
            1,
            CharNotInParser('{', '}')
        )

        val clogParser = SequenceParser(
            CharInParser('{'),
            rawParser,
            CharInParser('}')
        )

        val statementParser = ChoiceParser(
            rawParser,
            clogParser
        )

        val messageParser = ManyParser(
            statementParser
        )

        return messageParser
    }

    /*
     * Recursively-grouped, binary operation expression parser
     *
     * number     ::= digit+
     * factor     ::= '-'? (number | '(' expression ')')
     * term       ::= factor (('*'| '/') factor)*
     * expression ::= term   (('+'| '-') term  )*
     */
    private fun getArithmeticParser(): Parser {
        val number = AtLeastParser(1, DigitParser(), name = "number")

        val lazyExpressionParser = LazyParser()

        val factorParser = SequenceParser(
            MaybeParser(
                CharInParser('-')
            ),
            ChoiceParser(
                number,
                SequenceParser(
                    CharInParser('(', name = "("),
                    lazyExpressionParser,
                    CharInParser(')', name = ")")
                )
            )
        )

        val termParser = SequenceParser(
            factorParser,
            MaybeParser(
                ManyParser(
                    SequenceParser(
                        ChoiceParser(
                            CharInParser('*', name = "*"),
                            CharInParser('/', name = "/")
                        ),
                        factorParser
                    )
                )
            )
        )

        val expressionParser = SequenceParser(
            termParser,
            MaybeParser(
                ManyParser(
                    SequenceParser(
                        ChoiceParser(
                            CharInParser('+', name = "+"),
                            CharInParser('-', name = "-")
                        ),
                        termParser
                    )
                )
            )
        )

        lazyExpressionParser.parser = expressionParser

        return expressionParser
    }

    /*
     * Code island parser using a arithmetic expression as the island contents
     */
    private fun getClogExpressionParser(): Parser {
        val rawParser = AtLeastParser(
            1,
            CharNotInParser('{', '}')
        )

        val clogParser = SequenceParser(
            CharInParser('{'),
            getArithmeticParser(),
            CharInParser('}')
        )

        val statementParser = ChoiceParser(
            rawParser,
            clogParser
        )

        val messageParser = ManyParser(
            statementParser
        )

        return messageParser
    }
}
