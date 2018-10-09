package com.copperleaf.kudzu.parser

import com.copperleaf.kudzu.Node
import com.copperleaf.kudzu.Parser
import com.copperleaf.kudzu.ParserContext

class ExpressionParser(termParser: Parser, name: String = "") : Parser(name) {

    val parser: Parser = setupExpressionParserWithFixedLevels(termParser)

    override fun predict(input: ParserContext): Boolean = parser.predict(input)

    override fun parse(input: ParserContext): Pair<Node, ParserContext> = parser.parse(input)

    private fun setupExpressionParserWithFixedLevels(termParser: Parser): Parser {
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
                termParser,
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


// Adapted from jparsec OperatorTable class
//----------------------------------------------------------------------------------------------------------------------

class OperatorTable(
        private val operand: Parser,
        private vararg val operators: Operator,
        name: String = ""
) : Parser(name) {

    private val expressionParser = buildExpressionParser()

    private fun buildExpressionParser(): Parser {
        if (operators.isEmpty()) return operand

        var startIndex = 0
        var endIndex = 0
        var precedence = operators.first().precedence
        var associativity = operators.first().associativity
        var expressionParser = operand
        for (i in 1 until operators.size) {
            val op = operators[i]
            endIndex = i
            if (op.precedence == precedence && op.associativity == associativity) {
                continue
            }
            endIndex = i
            expressionParser = buildSubExpression(getOperatorParser(startIndex, endIndex), associativity, expressionParser)
            startIndex = i
            precedence = operators[i].precedence
            associativity = operators[i].associativity
        }
        if (endIndex != operators.size) {
            endIndex = operators.size
            associativity = operators[startIndex].associativity
            expressionParser = buildSubExpression(getOperatorParser(startIndex, endIndex), associativity, expressionParser)
        }
        return expressionParser
    }

    private fun getOperatorParser(begin: Int, end: Int): Parser {
        return ChoiceParser(*operators.slice(begin until end).map { it.op }.toTypedArray(), name="op")
    }

    private fun buildSubExpression(operator: Parser, associativity: Associativity, operand: Parser): Parser {
        return when (associativity) {
            Associativity.PREFIX  -> SequenceParser(ManyParser(operator), operand, name="prefix")
            Associativity.POSTFIX -> SequenceParser(operand, ManyParser(operator), name="postfix")
            Associativity.INFIX   -> SequenceParser(operand, ManyParser(SequenceParser(operator, operand)), name="infix")
        }
    }

// Parse with this expression
//----------------------------------------------------------------------------------------------------------------------

    override fun predict(input: ParserContext): Boolean = expressionParser.predict(input)

    override fun parse(input: ParserContext): Pair<Node, ParserContext> = expressionParser.parse(input)
}

private enum class Associativity {
    PREFIX, POSTFIX, INFIX
}

sealed class Operator(
        val op: Parser,
        val precedence: Int,
        val associativity: Associativity
) : Comparable<Operator> {

    /** Higher precedence first. For tie, compares associativity.  */
    override fun compareTo(other: Operator): Int {
        if (precedence > other.precedence) return -1
        return if (precedence < other.precedence) 1 else associativity.compareTo(other.associativity)
    }
}

class PrefixOperator(op: Parser, precedence: Int) : Operator(op, precedence, Associativity.PREFIX)
class PostfixOperator(op: Parser, precedence: Int) : Operator(op, precedence, Associativity.POSTFIX)
class InfixOperator(op: Parser, precedence: Int) : Operator(op, precedence, Associativity.INFIX)