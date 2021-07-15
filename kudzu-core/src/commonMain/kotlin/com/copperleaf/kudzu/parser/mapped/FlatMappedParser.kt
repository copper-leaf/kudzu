package com.copperleaf.kudzu.parser.mapped

import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.parser.Parser
import com.copperleaf.kudzu.parser.ParserContext
import com.copperleaf.kudzu.parser.ParserResult

/**
 * Convert the Node from a Parser into a Node of a different type.
 */
@ExperimentalStdlibApi
class FlatMappedParser<InputParserNodeType : Node, OutputParserNodeType : Node>(
    val parser: Parser<InputParserNodeType>,
    val mapperFunction: ParserContext.(InputParserNodeType) -> OutputParserNodeType,
) : Parser<OutputParserNodeType> {
    override fun predict(input: ParserContext): Boolean {
        return parser.predict(input)
    }

    override val parse = DeepRecursiveFunction<ParserContext, ParserResult<OutputParserNodeType>> { input ->
        val result = parser.parse.callRecursive(input)
        val mappedNode = mapperFunction(input, result.first)

        mappedNode to result.second
    }
}
