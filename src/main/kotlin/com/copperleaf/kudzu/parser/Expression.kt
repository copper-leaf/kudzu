package com.copperleaf.kudzu.parser

import com.copperleaf.kudzu.Node
import com.copperleaf.kudzu.NonTerminalNode
import com.copperleaf.kudzu.Parser
import com.copperleaf.kudzu.ParserContext
import com.copperleaf.kudzu.Visitor
import com.copperleaf.kudzu.VisitorContext
import com.copperleaf.kudzu.child
import com.copperleaf.kudzu.find
import com.copperleaf.kudzu.hasChild

class ExpressionParser(termParser: Parser, vararg operators: Operator, name: String = "") : Parser(name) {

    constructor(
            termParser: Parser,
            operators: List<EvaluatableOperator<*>>,
            name: String = ""
    ) : this(
            termParser,
            *operators.map { it.op }.toTypedArray(),
            name = name
    )

    val parser: Parser = setupExpressionParserWithFixedLevels(termParser, operators.toList())

    override fun predict(input: ParserContext): Boolean = parser.predict(input)

    override fun parse(input: ParserContext): Pair<Node, ParserContext> = parser.parse(input)

    private fun setupExpressionParserWithFixedLevels(termParser: Parser, operators: List<Operator>): Parser {
        return SequenceParser(
                operators
                        .groupBy { OperatorLevel(it.precedence, it.associativity) }
                        .toSortedMap(reverseOrder())
                        .values
                        .fold(termParser) { lastParser, currentLevelOperators ->
                            createParserLevel(lastParser, currentLevelOperators)
                        },
                name = "expressionRoot"
        )
    }

    private fun createParserLevel(operand: Parser, operators: List<Operator>): Parser {
        val operator = operators.toParser()

        return when (operators.first()) {
            is PrefixOperator  -> SequenceParser(
                    NamedParser(
                            ManyParser(NamedParser(operator, "operator")),
                            name = "operators"
                    ),
                    NamedParser(
                            operand,
                            "operand"
                    ),
                    name = "prefix"
            )
            is PostfixOperator -> SequenceParser(
                    NamedParser(
                            operand,
                            "operand"
                    ),
                    NamedParser(
                            ManyParser(operator),
                            name = "operators"
                    ),
                    name = "postfix"
            )
            is InfixrOperator  -> LazyParser {
                SequenceParser(
                        NamedParser(
                                operand,
                                "operand"
                        ),
                        NamedParser(
                                MaybeParser(
                                        SequenceParser(
                                                NamedParser(operator, "operator"),
                                                NamedParser(this, "operand")
                                        )
                                ),
                                "operators"
                        ),
                        name = "infixr"
                )
            }
            is InfixOperator   -> SequenceParser(
                    NamedParser(
                            operand,
                            "operand"
                    ),
                    NamedParser(
                            ManyParser(
                                    SequenceParser(
                                            NamedParser(operator, "operator"),
                                            NamedParser(operand, "operand")
                                    )
                            ),
                            "operators"
                    ),
                    name = "infix"
            )
        }
    }

    private fun List<Operator>.toParser(): Parser {
        return ChoiceParser(*this.map { it.parser }.toTypedArray(), name = "op")
    }

}

sealed class Operator(
        val parser: Parser,
        val precedence: Int,
        val associativity: Int
)

class PrefixOperator(op: Parser, precedence: Int) : Operator(op, precedence, 1)
class PostfixOperator(op: Parser, precedence: Int) : Operator(op, precedence, 2)
class InfixrOperator(op: Parser, precedence: Int) : Operator(op, precedence, 3)
class InfixOperator(op: Parser, precedence: Int) : Operator(op, precedence, 4)

private data class OperatorLevel(val precedence: Int, val associativity: Int) : Comparable<OperatorLevel> {
    override fun compareTo(other: OperatorLevel): Int {
        val comparePrecedence = precedence.compareTo(other.precedence)
        val compareAssociativity = associativity.compareTo(other.associativity)
        return if (comparePrecedence != 0) comparePrecedence else compareAssociativity
    }
}

// Expression Evaluation
//----------------------------------------------------------------------------------------------------------------------

sealed class EvaluatableOperator<U>(
        val op: Operator
)

class PrefixEvaluatableOperator<U>(op: PrefixOperator, val eval: (rhs: U) -> U) : EvaluatableOperator<U>(op) {
    constructor(op: Parser, precedence: Int, eval: (rhs: U) -> U) : this(PrefixOperator(op, precedence), eval)
}

class PostfixEvaluatableOperator<U>(op: PostfixOperator, val eval: (lhs: U) -> U) : EvaluatableOperator<U>(op) {
    constructor(op: Parser, precedence: Int, eval: (rhs: U) -> U) : this(PostfixOperator(op, precedence), eval)
}

class InfixrEvaluatableOperator<U>(op: InfixrOperator, val eval: (lhs: U, rhs: U) -> U) : EvaluatableOperator<U>(op) {
    constructor(op: Parser, precedence: Int, eval: (lhs: U, rhs: U) -> U) : this(InfixrOperator(op, precedence), eval)
}

class InfixEvaluatableOperator<U>(op: InfixOperator, val eval: (lhs: U, rhs: U) -> U) : EvaluatableOperator<U>(op) {
    constructor(op: Parser, precedence: Int, eval: (lhs: U, rhs: U) -> U) : this(InfixOperator(op, precedence), eval)
}

open class ExpressionVisitor<T : ExpressionContext<U>, U>(
        val evaluators: List<EvaluatableOperator<U>>,
        val defaultValue: (Node) -> U
) : Visitor<T>(NonTerminalNode::class, "expressionRoot") {

    override fun visit(context: T, node: Node) {
        context.value = getValue(node)
    }

    private fun getValue(node: Node): U {
        return when (node.name) {
            "prefix"         -> getPrefixValue(node as SequenceNode)
            "postfix"        -> getPostfixValue(node as SequenceNode)
            "infixr"         -> getInfixrValue(node as SequenceNode)
            "infix"          -> getInfixValue(node as SequenceNode)
            "expressionRoot" -> getValue((node as SequenceNode).child())
            else             -> defaultValue(node)
        }
    }

    private fun getPrefixValue(node: NonTerminalNode): U {
        val initialNode = node.find<NamedNode>("operand").child()
        val initialValue = getValue(initialNode)

        var rhsValue = initialValue

        val operations = node.find<NamedNode>("operators").child<ManyNode>()
        for (operationNode in operations.children) {
            val operator = operationNode.text

            val evaluator = evaluators
                    .filter { it is PrefixEvaluatableOperator<*> }
                    .find { operator == it.op.parser.name }
                    as PrefixEvaluatableOperator<U>

            rhsValue = evaluator.eval(rhsValue)
        }

        return rhsValue
    }

    private fun getPostfixValue(node: NonTerminalNode): U {
        val initialNode = node.find<NamedNode>("operand").child()
        val initialValue = getValue(initialNode)

        var lhsValue = initialValue

        val operations = node.find<NamedNode>("operators").child<ManyNode>()
        for (operationNode in operations.children) {
            val operator = operationNode.text

            val evaluator = evaluators
                    .filter { it is PostfixEvaluatableOperator<*> }
                    .find { operator == it.op.parser.name }
                    as PostfixEvaluatableOperator<U>

            lhsValue = evaluator.eval(lhsValue)
        }

        return lhsValue
    }

    private fun getInfixrValue(node: NonTerminalNode): U {
        val initialNode = node.find<NamedNode>("operand").child()
        val initialValue = getValue(initialNode)

        var lhsValue = initialValue

        if (node.find<NamedNode>("operators").child<MaybeNode>().hasChild()) {
            val operationNode = node.find<NamedNode>("operators").child<MaybeNode>().child()
            val operator = operationNode.find<NamedNode>("operator").child().text

            val rhs = operationNode.find<NamedNode>("operand").child()
            val rhsValue = getValue(rhs)

            val evaluator = evaluators
                    .filter { it is InfixrEvaluatableOperator<*> }
                    .find { operator == it.op.parser.name }
                    as InfixrEvaluatableOperator<U>

            lhsValue = evaluator.eval(lhsValue, rhsValue)
        }

        return lhsValue
    }

    private fun getInfixValue(node: NonTerminalNode): U {
        val initialNode = node.find<NamedNode>("operand").child()
        val initialValue = getValue(initialNode)

        var lhsValue = initialValue

        val operations = node.find<NamedNode>("operators").child<ManyNode>()
        for (operationNode in operations.children) {
            val operator = operationNode.find<NamedNode>("operator").child().text

            val rhs = operationNode.find<NamedNode>("operand").child()
            val rhsValue = getValue(rhs)

            val evaluator = evaluators
                    .filter { it is InfixEvaluatableOperator<*> }
                    .find { operator == it.op.parser.name }
                    as InfixEvaluatableOperator<U>

            lhsValue = evaluator.eval(lhsValue, rhsValue)
        }

        return lhsValue
    }

}

open class ExpressionContext<T> : VisitorContext {

    var value: T? = null

}