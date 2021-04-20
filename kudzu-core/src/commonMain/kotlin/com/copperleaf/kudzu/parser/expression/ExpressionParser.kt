package com.copperleaf.kudzu.parser.expression

import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.node.mapped.ValueNode
import com.copperleaf.kudzu.parser.Parser
import com.copperleaf.kudzu.parser.ParserContext
import com.copperleaf.kudzu.parser.ParserResult
import com.copperleaf.kudzu.parser.choice.ExactChoiceParser

@ExperimentalStdlibApi
class ExpressionParser<T: Any>(
    private val termParser: Parser<ValueNode<T>>,
    private vararg val operators: Operator<T>,
) : Parser<Node>() {

    private val parser: Parser<Node> by lazy {
        val unaryOperators = operators.filterIsInstance<Operator.UnaryOperator<T>>()
        check(unaryOperators.map { it.name }.distinct().size == unaryOperators.size) { "Operators must be unique" }
        val binaryOperators = operators.filterIsInstance<Operator.UnaryOperator<T>>()
        check(binaryOperators.map { it.name }.distinct().size == binaryOperators.size) { "Operators must be unique" }

        val operatorsGroupedByPrecedenceAndAssociativity: Map<Operator.Precedence, List<Operator<T>>> = operators
            .groupBy { it.precedence }

        val operatorsSortedIntoDiscreteLevels: List<List<Operator<T>>> = operatorsGroupedByPrecedenceAndAssociativity
            .entries
            .sortedByDescending { it.key }
            .map { it.value }

        val operatorsLevelsFoldedIntoExpressionParser: Parser<Node> = operatorsSortedIntoDiscreteLevels
            .fold(termParser as Parser<Node>) { lastParser, currentLevelOperators ->
                createParserLevel(lastParser, currentLevelOperators)
            }

        operatorsLevelsFoldedIntoExpressionParser
    }

    val evaluator: ExpressionEvaluator<T> by lazy {
        ExpressionEvaluator(*operators)
    }

    override fun predict(input: ParserContext): Boolean = parser.predict(input)

    override val parse: DeepRecursiveFunction<ParserContext, ParserResult<Node>> = parser.parse

    companion object {
        private fun <T: Any> createParserLevel(operand: Parser<Node>, operators: List<Operator<T>>): Parser<Node> {
            val actualOperatorParserForLevel = ExactChoiceParser(*operators.map { it.parser }.toTypedArray())

            val operatorParserLevelParser: Parser<*> = when(operators.first()) {
                is Operator.Prefix<T> -> PrefixOperatorParser(actualOperatorParserForLevel, operand)
                is Operator.Postfix<T> -> PostfixOperatorParser(actualOperatorParserForLevel, operand)
                is Operator.Infixr<T> -> InfixrOperatorParser(actualOperatorParserForLevel, operand)
                is Operator.Infix<T> -> InfixOperatorParser(actualOperatorParserForLevel, operand)
                else -> error("") // should not get here
            }

            return operatorParserLevelParser as Parser<Node>
        }
    }
}
