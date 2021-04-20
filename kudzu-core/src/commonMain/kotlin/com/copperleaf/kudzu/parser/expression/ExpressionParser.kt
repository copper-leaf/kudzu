package com.copperleaf.kudzu.parser.expression

import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.parser.Parser
import com.copperleaf.kudzu.parser.ParserContext
import com.copperleaf.kudzu.parser.ParserResult
import com.copperleaf.kudzu.parser.choice.ExactChoiceParser

@ExperimentalStdlibApi
class ExpressionParser(
    private val termParser: Parser<*>,
    private vararg val operators: Operator,
) : Parser<Node>() {

    private val parser: Parser<Node> by lazy {
        val operatorsGroupedByPrecedenceAndAssociativity: Map<Operator.Precedence, List<Operator>> = operators
            .groupBy { it.precedence }

        val operatorsSortedIntoDiscreteLevels: List<List<Operator>> = operatorsGroupedByPrecedenceAndAssociativity
            .entries
            .sortedByDescending { it.key }
            .map { it.value }

        val operatorsLevelsFoldedIntoExpressionParser: Parser<Node> = operatorsSortedIntoDiscreteLevels
            .fold(termParser as Parser<Node>) { lastParser, currentLevelOperators ->
                createParserLevel(lastParser, currentLevelOperators)
            }

        operatorsLevelsFoldedIntoExpressionParser
    }

    override fun predict(input: ParserContext): Boolean = parser.predict(input)

    override val parse: DeepRecursiveFunction<ParserContext, ParserResult<Node>> = parser.parse

    private fun createParserLevel(operand: Parser<Node>, operators: List<Operator>): Parser<Node> {
        val actualOperatorParserForLevel = ExactChoiceParser(*operators.map { it.parser }.toTypedArray())

        val operatorParserLevelParser: Parser<*> = when(operators.first()) {
            is Operator.Prefix -> PrefixOperatorParser(actualOperatorParserForLevel, operand)
            is Operator.Postfix -> PostfixOperatorParser(actualOperatorParserForLevel, operand)
            is Operator.Infixr -> InfixrOperatorParser(actualOperatorParserForLevel, operand)
            is Operator.Infix -> InfixOperatorParser(actualOperatorParserForLevel, operand)
        }

        return operatorParserLevelParser as Parser<Node>
    }
}
