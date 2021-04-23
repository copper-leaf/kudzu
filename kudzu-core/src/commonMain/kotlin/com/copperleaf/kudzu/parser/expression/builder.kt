package com.copperleaf.kudzu.parser.expression

import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.node.expression.ExpressionNode
import com.copperleaf.kudzu.node.mapped.ValueNode
import com.copperleaf.kudzu.parser.Parser
import com.copperleaf.kudzu.parser.chars.CharInParser
import com.copperleaf.kudzu.parser.choice.ExactChoiceParser
import com.copperleaf.kudzu.parser.choice.PredictiveChoiceParser
import com.copperleaf.kudzu.parser.mapped.FlatMappedParser
import com.copperleaf.kudzu.parser.sequence.SequenceParser

object ExpressionParserBuilder {
    @ExperimentalStdlibApi
    fun <T : Any> checkOperatorsAreValid(
        operators: List<Operator<T>>
    ) {
        val unaryOperators = operators.filterIsInstance<Operator.UnaryOperator<T>>()
        check(unaryOperators.map { it.name }.distinct().size == unaryOperators.size) { "Operators must be unique" }
        val binaryOperators = operators.filterIsInstance<Operator.UnaryOperator<T>>()
        check(binaryOperators.map { it.name }.distinct().size == binaryOperators.size) { "Operators must be unique" }
    }

    @ExperimentalStdlibApi
    fun <T : Any> createRootTermParser(
        expressionParser: Parser<*>,
        termParser: Parser<ValueNode<T>>,
        parenthesizedTerm: Boolean = true
    ): Parser<Node> {
        return if (parenthesizedTerm) {
            val parenthesizedExpressionParser = FlatMappedParser(
                SequenceParser(
                    CharInParser('('),
                    expressionParser,
                    CharInParser(')'),
                )
            ) {
                val (_, exprNode, _) = it.children
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

            unwrappedChoiceParser as Parser<Node>
        } else {
            termParser as Parser<Node>
        }
    }

    @ExperimentalStdlibApi
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

    @ExperimentalStdlibApi
    fun <T : Any> createExpressionParser(
        expressionParser: Parser<*>,
        termParser: Parser<ValueNode<T>>,
        operators: List<Operator<T>>,
        parenthesizedTerm: Boolean,
        simplifyAst: Boolean
    ): Parser<Node> {
        checkOperatorsAreValid(operators)

        val operatorsGroupedByPrecedenceAndAssociativity: Map<Operator.Precedence, List<Operator<T>>> = operators
            .groupBy { it.precedence }

        val operatorsSortedIntoDiscreteLevels: List<List<Operator<T>>> = operatorsGroupedByPrecedenceAndAssociativity
            .entries
            .sortedByDescending { it.key }
            .map { it.value }

        val actualTermParser: Parser<Node> = createRootTermParser(expressionParser, termParser, parenthesizedTerm)

        val operatorsLevelsFoldedIntoExpressionParser: Parser<Node> = operatorsSortedIntoDiscreteLevels
            .fold(actualTermParser) { lastParser, currentLevelOperators ->
                createParserLevel(lastParser, currentLevelOperators)
            }

        val maybeSimplifiedExpressionParser = if(simplifyAst) {
            FlatMappedParser(
                operatorsLevelsFoldedIntoExpressionParser
            ) {
                if(it is ExpressionNode) {
                    it.simplify()
                }
                else {
                    it
                }
            }
        }
        else {
            operatorsLevelsFoldedIntoExpressionParser
        }

        return maybeSimplifiedExpressionParser
    }
}
