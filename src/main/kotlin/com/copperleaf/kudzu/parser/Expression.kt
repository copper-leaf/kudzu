package com.copperleaf.kudzu.parser

import com.copperleaf.kudzu.Node
import com.copperleaf.kudzu.Parser
import com.copperleaf.kudzu.ParserContext

class ExpressionParser(name: String = "") : Parser(name) {

    val parser: Parser = setupExpressionParserWithFixedLevels()

    override fun predict(input: ParserContext): Boolean = parser.predict(input)

    override fun parse(input: ParserContext): Pair<Node, ParserContext> = parser.parse(input)

    private fun setupExpressionParserWithFixedLevels(): Parser {
        val EParser = LazyParser(name = "EParser")
        val E = LazyParser(name = "E")
        val T = LazyParser(name = "T")
        val F = LazyParser(name = "F")
        val P = LazyParser(name = "P")

        val _E = SequenceParser(
                T,
                ManyParser(
                        SequenceParser(
                                CharInParser('+', '-'),
                                T,
                                name = "add sub"
                        ),
                        name = "*add sub"
                ),
                name = "_E"
        )
        E.parser = _E

        val _T = SequenceParser(
                F,
                ManyParser(
                        SequenceParser(
                                CharInParser('*', '/'),
                                F,
                                name = "mul div"
                        ),
                        name = "*mul div"
                ),
                name = "_T"
        )
        T.parser = _T

        val _F = SequenceParser(
                P,
                MaybeParser(
                        SequenceParser(
                                CharInParser('^'),
                                F,
                                name = "power"
                        ),
                        name = "?power"
                ),
                name = "_F"
        )
        F.parser = _F

        val _P = ChoiceParser(
                CharParser(name = "val"),
                SequenceParser(
                        CharInParser('('),
                        E,
                        CharInParser(')'),
                        name = "parentheses"
                ),
                SequenceParser(
                        CharInParser('-'),
                        F,
                        name = "unary minus"
                ),
                name = "_P"
        )
        P.parser = _P

        EParser.parser = E

        return EParser
    }

}
