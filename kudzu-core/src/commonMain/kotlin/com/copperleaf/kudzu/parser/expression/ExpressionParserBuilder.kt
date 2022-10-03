package com.copperleaf.kudzu.parser.expression

import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.node.expression.ExpressionNode
import com.copperleaf.kudzu.node.expression.RootExpressionNode
import com.copperleaf.kudzu.node.mapped.ValueNode
import com.copperleaf.kudzu.parser.Parser
import com.copperleaf.kudzu.parser.choice.ExactChoiceParser
import com.copperleaf.kudzu.parser.choice.PredictiveChoiceParser
import com.copperleaf.kudzu.parser.mapped.FlatMappedParser
import com.copperleaf.kudzu.parser.sequence.SequenceParser
import com.copperleaf.kudzu.parser.text.LiteralTokenParser

@Suppress("UNCHECKED_CAST")
object ExpressionParserBuilder {
    fun <T : Any> checkOperatorsAreValid(
        operators: List<Operator<T>>
    ) {
        val unaryOperators = operators.filterIsInstance<Operator.UnaryOperator<T>>()
        check(unaryOperators.map { it.name }.distinct().size == unaryOperators.size) { "Operators must be unique" }
        val binaryOperators = operators.filterIsInstance<Operator.BinaryOperator<T>>()
        check(binaryOperators.map { it.name }.distinct().size == binaryOperators.size) { "Operators must be unique" }
    }

    fun <T : Any> createRootTermParser(
        expressionParser: Parser<*>,
        termParser: Parser<ValueNode<T>>,
        parenthesizedTerm: Boolean = true
    ): Parser<Node> {
        return if (parenthesizedTerm) {
            val parenthesizedExpressionParser = FlatMappedParser(
                SequenceParser(
                    LiteralTokenParser("("),
                    expressionParser,
                    LiteralTokenParser(")"),
                )
            ) { (_, _, exprNode, _) ->
                exprNode
            }

            val unwrappedChoiceParser = FlatMappedParser(
                PredictiveChoiceParser(
                    parenthesizedExpressionParser,
                    termParser
                )
            ) {
                it.node
            }

            unwrappedChoiceParser
        } else {
            termParser as Parser<Node>
        }
    }

    fun <T : Any> createParserLevel(operand: Parser<Node>, operators: List<Operator<T>>): Parser<Node> {
        val actualOperatorParserForLevel = ExactChoiceParser(*operators.map { it.parser }.toTypedArray())

        val operatorParserLevelParser: Parser<*> = when (operators.first()) {
            is Operator.Prefix<T> -> PrefixOperatorParser(actualOperatorParserForLevel, operand)
            is Operator.Postfix<T> -> PostfixOperatorParser(actualOperatorParserForLevel, operand)
            is Operator.Infixr<T> -> InfixrOperatorParser(actualOperatorParserForLevel, operand)
            is Operator.Infix<T> -> InfixOperatorParser(actualOperatorParserForLevel, operand)
            else -> error("") // should not get here
        }

        return operatorParserLevelParser as Parser<Node>
    }

    fun <T : Any> createExpressionParser(
        expressionParser: Parser<*>,
        termParser: Parser<ValueNode<T>>,
        operators: List<Operator<T>>,
        parenthesizedTerm: Boolean,
        simplifyAst: Boolean
    ): Parser<RootExpressionNode> {
        val expandedOperatorList = operators.flatMap { it.expandAliases() }

        checkOperatorsAreValid(expandedOperatorList)

        val operatorsGroupedByPrecedenceAndAssociativity: Map<Operator.Precedence, List<Operator<T>>> =
            expandedOperatorList
                .groupBy { it.precedence }

        val operatorsSortedIntoDiscreteLevels: List<List<Operator<T>>> =
            operatorsGroupedByPrecedenceAndAssociativity
                .entries
                .sortedByDescending { it.key }
                .map { it.value }

        val actualTermParser: Parser<Node> = createRootTermParser(expressionParser, termParser, parenthesizedTerm)

        val operatorsLevelsFoldedIntoExpressionParser: Parser<Node> = operatorsSortedIntoDiscreteLevels
            .fold(actualTermParser) { lastParser, currentLevelOperators ->
                createParserLevel(lastParser, currentLevelOperators)
            }

        val maybeSimplifiedExpressionParser = if (simplifyAst) {
            FlatMappedParser(
                operatorsLevelsFoldedIntoExpressionParser
            ) {
                if (it is ExpressionNode) {
                    it.simplify(it)
                } else {
                    it
                }
            }
        } else {
            operatorsLevelsFoldedIntoExpressionParser
        }

        return FlatMappedParser(maybeSimplifiedExpressionParser) { RootExpressionNode(it, it.context) }
    }
}
