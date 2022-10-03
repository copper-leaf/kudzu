package com.copperleaf.kudzu.parser.named

import com.copperleaf.kudzu.RemapperFn
import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.node.named.NamedNode
import com.copperleaf.kudzu.parseWithRemappedErrors
import com.copperleaf.kudzu.parser.Parser
import com.copperleaf.kudzu.parser.ParserContext
import com.copperleaf.kudzu.parser.ParserResult

/**
 * Annotates a Node to give it a name, for easily identifying specific nodes in the AST. This Parser wraps another, and
 * the resulting node in wrapped in [NamedNode] with the given name.
 *
 * Predicts true when:
 *   - [parser] predicts true
 *
 * Parsing fails when:
 *   - [parser] fails to parse
 */
class NamedParser<T : Node>(
    val parser: Parser<T>,
    val name: String,
    private val remapErrors: RemapperFn = { _, e -> e },
) : Parser<NamedNode<T>> {

    override fun predict(input: ParserContext): Boolean = parser.predict(input)

    override val parse = DeepRecursiveFunction<ParserContext, ParserResult<NamedNode<T>>> { input ->
        val node = parseWithRemappedErrors(parser, input, remapErrors)
        NamedNode(node.first, name) to node.second
    }
}
