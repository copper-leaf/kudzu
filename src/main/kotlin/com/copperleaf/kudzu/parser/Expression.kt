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
            operators: List<EvaluableOperator<*>>,
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
        return operators.first().create(operator, operand)
    }

    private fun List<Operator>.toParser(): Parser {
        return ChoiceParser(*this.map { it.parser }.toTypedArray(), name = "op")
    }

}

sealed class Operator(
        val parser: Parser,
        val precedence: Int,
        val associativity: Int
) {
    abstract fun create(operator: Parser, operand: Parser): Parser
}

class PrefixOperator(op: Parser, precedence: Int) : Operator(op, precedence, 1) {
    override fun create(operator: Parser, operand: Parser): Parser {
        return SequenceParser(
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
    }
}

class PostfixOperator(op: Parser, precedence: Int) : Operator(op, precedence, 2) {
    override fun create(operator: Parser, operand: Parser): Parser {
        return SequenceParser(
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
    }
}

class InfixrOperator(op: Parser, precedence: Int) : Operator(op, precedence, 3) {
    override fun create(operator: Parser, operand: Parser): Parser {
        return LazyParser {
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
    }
}

class InfixOperator(op: Parser, precedence: Int) : Operator(op, precedence, 4) {
    override fun create(operator: Parser, operand: Parser): Parser {
        return SequenceParser(
            NamedParser(
                operand,
                "operand"
            ),
            NamedParser(
                MaybeParser(
                    ManyParser(
                        SequenceParser(
                            NamedParser(operator, "operator"),
                            NamedParser(operand, "operand")
                        )
                    )
                ),
                "operators"
            ),
            name = "infix"
        )
    }
}


private data class OperatorLevel(val precedence: Int, val associativity: Int) : Comparable<OperatorLevel> {
    override fun compareTo(other: OperatorLevel): Int {
        val comparePrecedence = precedence.compareTo(other.precedence)
        val compareAssociativity = associativity.compareTo(other.associativity)
        return if (comparePrecedence != 0) comparePrecedence else compareAssociativity
    }
}

// Expression Evaluation
//----------------------------------------------------------------------------------------------------------------------

sealed class EvaluableOperator<U>(
        val op: Operator
)

class PrefixEvaluableOperator<U>(op: PrefixOperator, val eval: (rhs: U) -> U) : EvaluableOperator<U>(op) {
    constructor(op: Parser, precedence: Int, eval: (rhs: U) -> U) : this(PrefixOperator(op, precedence), eval)
}

class PostfixEvaluableOperator<U>(op: PostfixOperator, val eval: (lhs: U) -> U) : EvaluableOperator<U>(op) {
    constructor(op: Parser, precedence: Int, eval: (rhs: U) -> U) : this(PostfixOperator(op, precedence), eval)
}

class InfixrEvaluableOperator<U>(op: InfixrOperator, val eval: (lhs: U, rhs: U) -> U) : EvaluableOperator<U>(op) {
    constructor(op: Parser, precedence: Int, eval: (lhs: U, rhs: U) -> U) : this(InfixrOperator(op, precedence), eval)
}

class InfixEvaluableOperator<U>(op: InfixOperator, val eval: (lhs: U, rhs: U) -> U) : EvaluableOperator<U>(op) {
    constructor(op: Parser, precedence: Int, eval: (lhs: U, rhs: U) -> U) : this(InfixOperator(op, precedence), eval)
}

open class ExpressionVisitor<T : ExpressionContext<U>, U>(
        val evaluators: List<EvaluableOperator<U>>,
        val defaultValue: (T, Node) -> U
) : Visitor<T>(NonTerminalNode::class, "expressionRoot") {

    override fun visit(context: T, node: Node) {
        context.value = getValue(context, node)
    }

    private fun getValue(context: T, node: Node): U {
        return when (node.name) {
            "prefix"         -> getPrefixValue(context, node as SequenceNode)
            "postfix"        -> getPostfixValue(context, node as SequenceNode)
            "infixr"         -> getInfixrValue(context, node as SequenceNode)
            "infix"          -> getInfixValue(context, node as SequenceNode)
            "expressionRoot" -> getValue(context, (node as SequenceNode).child())
            else             -> defaultValue(context, node)
        }
    }

    private fun getPrefixValue(context: T, node: NonTerminalNode): U {
        val initialNode = node.find<NamedNode>("operand").child()
        val initialValue = getValue(context, initialNode)

        var rhsValue = initialValue

        val operations = node.find<NamedNode>("operators").child<ManyNode>()
        for (operationNode in operations.children) {
            val operator = operationNode.child().child().name

            val evaluator = evaluators
                    .filter { it is PrefixEvaluableOperator<*> }
                    .find { operator == it.op.parser.name }
                    as PrefixEvaluableOperator<U>

            rhsValue = evaluator.eval(rhsValue)
        }

        return rhsValue
    }

    private fun getPostfixValue(context: T, node: NonTerminalNode): U {
        val initialNode = node.find<NamedNode>("operand").child()
        val initialValue = getValue(context, initialNode)

        var lhsValue = initialValue

        val operations = node.find<NamedNode>("operators").child<ManyNode>()
        for (operationNode in operations.children) {
            val operator = operationNode.child().name

            val evaluator = evaluators
                    .filter { it is PostfixEvaluableOperator<*> }
                    .find { operator == it.op.parser.name }
                    as PostfixEvaluableOperator<U>

            lhsValue = evaluator.eval(lhsValue)
        }

        return lhsValue
    }

    private fun getInfixrValue(context: T, node: NonTerminalNode): U {
        val initialNode = node.find<NamedNode>("operand").child()
        val initialValue = getValue(context, initialNode)

        var lhsValue = initialValue

        if (node.find<NamedNode>("operators").child<MaybeNode>().hasChild()) {
            val operationNode = node.find<NamedNode>("operators").child<MaybeNode>().child()
            val operator = operationNode.find<NamedNode>("operator").child().child().name

            val rhs = operationNode.find<NamedNode>("operand").child()
            val rhsValue = getValue(context, rhs)

            val evaluator = evaluators
                    .filter { it is InfixrEvaluableOperator<*> }
                    .find { operator == it.op.parser.name }
                    as InfixrEvaluableOperator<U>

            lhsValue = evaluator.eval(lhsValue, rhsValue)
        }

        return lhsValue
    }

    private fun getInfixValue(context: T, node: NonTerminalNode): U {
        val initialNode = node.find<NamedNode>("operand").child()
        val initialValue = getValue(context, initialNode)

        var lhsValue = initialValue

        val operations = node.find<NamedNode>("operators").child<MaybeNode>()
        if(operations.hasChild<ManyNode>()) {
            for (operationNode in operations.child<ManyNode>().children) {
                val operator = operationNode.find<NamedNode>("operator").child().child().name

                val rhs = operationNode.find<NamedNode>("operand").child()
                val rhsValue = getValue(context, rhs)

                val evaluator = evaluators
                    .filter { it is InfixEvaluableOperator<*> }
                    .find { operator == it.op.parser.name }
                        as InfixEvaluableOperator<U>

                lhsValue = evaluator.eval(lhsValue, rhsValue)
            }
        }

        return lhsValue
    }

}

open class ExpressionContext<T> : VisitorContext {

    var value: T? = null

}