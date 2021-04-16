package com.copperleaf.kudzu.parser.mapped

import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.parser.Parser
import com.copperleaf.kudzu.parser.ParserContext
import com.copperleaf.kudzu.parser.ParserResult

class FlatMappedParser<InputParserNodeType: Node, OutputParserNodeType: Node>(
    val parser: Parser<InputParserNodeType>,
    val mapperFunction: (InputParserNodeType) -> OutputParserNodeType,
) : Parser<OutputParserNodeType>() {
    override fun predict(input: ParserContext): Boolean {
        return parser.predict(input)
    }

    override fun parse(input: ParserContext): ParserResult<OutputParserNodeType> {
        val result = parser.parse(input)
        val mappedNode = mapperFunction(result.first)

        return mappedNode to result.second
    }

}
