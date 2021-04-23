package com.copperleaf.kudzu.parser.expression

import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.node.mapped.ValueNode
import com.copperleaf.kudzu.parser.Parser
import com.copperleaf.kudzu.parser.ParserContext
import com.copperleaf.kudzu.parser.ParserResult
import com.copperleaf.kudzu.parser.lazy.LazyParser

/**
 * A configurable operator-precedence parser for a simple expression grammar. A list of operators with custom
 * associativity and precedence will construct the multi-leveled parser which will properly parse and evaluate a simple
 * expression.
 *
 * The parameterized type [T] is the value type this expression's Operators operate on.
 */
@ExperimentalStdlibApi
@Suppress("UNCHECKED_CAST")
class ExpressionParser<T : Any>(
    private val termParser: Parser<ValueNode<T>>,
    private val operators: List<Operator<T>>,
    private val parenthesizedTerm: Boolean = true
) : Parser<Node> {
    constructor(
        termParser: Parser<ValueNode<T>>,
        vararg operators: Operator<T>,
        parenthesizedTerm: Boolean = true
    ) : this(termParser, operators.toList(), parenthesizedTerm)

    private val parser: Parser<Node> by lazy {
        LazyParser<Node>().also {
            it uses ExpressionParserBuilder.createExpressionParser(
                it,
                termParser,
                operators,
                parenthesizedTerm
            )
        }
    }

    val evaluator: ExpressionEvaluator<T> by lazy {
        ExpressionEvaluatorImpl(operators)
    }

    override fun predict(input: ParserContext): Boolean = parser.predict(input)

    override val parse: DeepRecursiveFunction<ParserContext, ParserResult<Node>> = parser.parse
}
