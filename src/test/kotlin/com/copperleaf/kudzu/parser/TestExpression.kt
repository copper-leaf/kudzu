package com.copperleaf.kudzu.parser

import com.copperleaf.kudzu.Node
import com.copperleaf.kudzu.ParserContext
import com.copperleaf.kudzu.parsedCorrectly
import com.copperleaf.kudzu.thenLog
import org.junit.jupiter.api.Test
import strikt.api.expectThat

class TestExpression {

    @Test
    fun testFixedExpressionParser() {
        var input: String
        var output: Pair<Node, ParserContext>?
        val termParser = AtLeastParser(1, DigitParser(name = "val"))
        val underTest = ExpressionParser(termParser)

        input = "11 ^ 22 * 33 + 44 + 55"
        output = underTest.test(input, skipWhitespace = true)

        expectThat(output)
                .parsedCorrectly()
                .thenLog()
    }

    @Test
    fun testOperatorTableExpressionParser() {
        var input: String
        var output: Pair<Node, ParserContext>?

        val operatorTable = LazyParser()
        val valueParser = LazyParser()
        val termParser = LazyParser()

        val _operatorTable = OperatorTable(
                termParser,
                InfixOperator(WordParser("||"), 10),
                InfixOperator(WordParser("&&"), 20),
                InfixOperator(WordParser("=="), 30),
                InfixOperator(CharInParser('+'), 40),
                InfixOperator(CharInParser('-'), 40),
                PrefixOperator(CharInParser('-'), 50),
                InfixOperator(CharInParser('*'), 60),
                InfixOperator(CharInParser('/'), 60),
                InfixOperator(CharInParser('^'), 70)
        )
        operatorTable.parser = _operatorTable

        val _valueParser = CharParser(name = "val")
        valueParser.parser = _valueParser

        val _termParser = ChoiceParser(
                SequenceParser(
                        CharInParser('('),
                        operatorTable,
                        CharInParser(')'),
                        name = "parens"
                ),
                valueParser,
                name = "term"
        )
        termParser.parser = _termParser

        val underTest = operatorTable

        input = "a ^ b * c + d + e"
        output = underTest.test(input, skipWhitespace = true)

        expectThat(output)
                .parsedCorrectly()
                .thenLog()
    }

}
