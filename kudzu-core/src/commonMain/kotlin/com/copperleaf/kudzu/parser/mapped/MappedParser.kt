package com.copperleaf.kudzu.parser.mapped

import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.node.NodeContext
import com.copperleaf.kudzu.node.mapped.ValueNode
import com.copperleaf.kudzu.parser.Parser
import com.copperleaf.kudzu.parser.ParserContext
import com.copperleaf.kudzu.parser.ParserResult

/**
 * Convert a Node from a parser into an arbitrary value, wrapped in [ValueNode]. The result does not have to be another
 * Node. The the mapped result should be another node, consider using [FlatMappedParser] instead.
 */
@ExperimentalStdlibApi
class MappedParser<ParserNodeType : Node, T>(
    val parser: Parser<ParserNodeType>,
    val mapperFunction: ParserContext.(ParserNodeType) -> T,
) : Parser<ValueNode<T>> {
    override fun predict(input: ParserContext): Boolean {
        return parser.predict(input)
    }

    override val parse = DeepRecursiveFunction<ParserContext, ParserResult<ValueNode<T>>> { input ->
        val result = parser.parse.callRecursive(input)
        val mappedValue = mapperFunction(input, result.first)
        val valueNode = ValueNode(
            mappedValue,
            NodeContext(input, result.second)
        )

        valueNode to result.second
    }
}
