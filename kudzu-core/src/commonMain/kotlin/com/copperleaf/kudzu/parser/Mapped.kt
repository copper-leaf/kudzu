package com.copperleaf.kudzu.parser

import com.copperleaf.kudzu.*

class ValueNode<T : Any>(
    val value: T,
    name: String,
    context: NodeContext
) : TerminalNode(
    name,
    context
) {
    override val text: String get() = value.toString()
}

class MappedParser<T : Any>(
    val parser: Parser,
    name: String = "",
    val mapperFunction: (Node) -> T,
) : Parser(name) {
    override fun predict(input: ParserContext): Boolean {
        return parser.predict(input)
    }

    override fun parse(input: ParserContext): Pair<Node, ParserContext> {
        val result = parser.parse(input)
        val mappedValue = mapperFunction(result.first)
        val valueNode = ValueNode(
            mappedValue,
            name,
            NodeContext(input, result.second)
        )

        return valueNode to result.second
    }

}
