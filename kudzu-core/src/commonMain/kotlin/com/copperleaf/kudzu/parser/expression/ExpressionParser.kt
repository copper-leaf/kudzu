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
 *
 * If [parenthesizedTerm] is true, then the parser will be constructed such that "leaf terms" may also be
 * sub-expressions wrapped in parentheses (like `(1 + 2) * 3`). If false, only the exact term parser passed in will be
 * allowed as the term, which may useful for you to create your own sub-expression grammar.
 *
 * The typical AST for an expression can go quite deep due to the nature of how operator precedence levels ultimately
 * nest inside one another. For an expression with 4 operators, the AST for a simple value would be at least 4 nodes
 * deep, which makes it harder to understand the actual structure parsed, and makes subsequent evaluation slower. But if
 * [simplifyAst] is true, the actual expression parse-tree will be simplified such that all the intermediate nodes that
 * are not necessary are removed, leaving a simpler AST that still have the same structure and evaluates in exactly the
 * same way, but has many fewer AST nodes, and the only ones that remain would actually represent the smallest set of
 * real operations in the expression and the literal values used as their operands.
 */
@ExperimentalStdlibApi

class ExpressionParser<T : Any>(
    private val termParser: (Parser<Node>) -> Parser<ValueNode<T>>,
    private val operators: List<Operator<T>>,
    private val parenthesizedTerm: Boolean = true,
    private val simplifyAst: Boolean = true
) : Parser<Node> {
    constructor(
        termParser: (Parser<Node>) -> Parser<ValueNode<T>>,
        vararg operators: Operator<T>,
        parenthesizedTerm: Boolean = true,
        simplifyAst: Boolean = true
    ) : this(termParser, operators.toList(), parenthesizedTerm, simplifyAst)

    private val parser: Parser<Node> by lazy {
        LazyParser<Node>().also {
            it uses ExpressionParserBuilder.createExpressionParser(
                it,
                termParser(it),
                operators,
                parenthesizedTerm,
                simplifyAst
            )
        }
    }

    val evaluator: ExpressionEvaluator<T> by lazy {
        ExpressionEvaluatorImpl(operators)
    }

    override fun predict(input: ParserContext): Boolean = parser.predict(input)

    override val parse: DeepRecursiveFunction<ParserContext, ParserResult<Node>> = parser.parse
}
