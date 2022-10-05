package com.copperleaf.kudzu.parser.mapped

import com.copperleaf.kudzu.RemapperFn
import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.parseWithRemappedErrors
import com.copperleaf.kudzu.parser.ParseFunction
import com.copperleaf.kudzu.parser.Parser
import com.copperleaf.kudzu.parser.ParserContext
import com.copperleaf.kudzu.parser.runParser

/**
 * Convert the Node from a Parser into a Node of a different type.
 */
public class FlatMappedParser<InputParserNodeType : Node, OutputParserNodeType : Node>(
    public val parser: Parser<InputParserNodeType>,
    public val remapErrors: RemapperFn = { _, e -> e },
    public val mapperFunction: ParserContext.(InputParserNodeType) -> OutputParserNodeType,
) : Parser<OutputParserNodeType> {
    override fun predict(input: ParserContext): Boolean {
        return parser.predict(input)
    }

    override val parse: ParseFunction<OutputParserNodeType> = runParser { input ->
        val result = parseWithRemappedErrors(parser, input, remapErrors)
        val mappedNode = mapperFunction(input, result.first)

        mappedNode to result.second
    }
}
