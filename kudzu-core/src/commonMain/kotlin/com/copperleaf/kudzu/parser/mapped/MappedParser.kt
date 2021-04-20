package com.copperleaf.kudzu.parser.mapped

import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.node.NodeContext
import com.copperleaf.kudzu.node.mapped.ValueNode
import com.copperleaf.kudzu.parser.Parser
import com.copperleaf.kudzu.parser.ParserContext
import com.copperleaf.kudzu.parser.ParserResult

@ExperimentalStdlibApi
class MappedParser<T : Any, ParserNodeType : Node>(
    val parser: Parser<ParserNodeType>,
    val mapperFunction: (ParserNodeType) -> T,
) : Parser<ValueNode<T>>() {
    override fun predict(input: ParserContext): Boolean {
        return parser.predict(input)
    }

    override val parse = DeepRecursiveFunction<ParserContext, ParserResult<ValueNode<T>>> { input ->
        val result = parser.parse.callRecursive(input)
        val mappedValue = mapperFunction(result.first)
        val valueNode = ValueNode(
            mappedValue,
            NodeContext(input, result.second)
        )

        valueNode to result.second
    }
}
