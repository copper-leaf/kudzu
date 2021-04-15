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

class MappedParser<T : Any, ParserNodeType: Node>(
    val parser: Parser<ParserNodeType>,
    name: String = "",
    val mapperFunction: (ParserNodeType) -> T,
) : Parser<ValueNode<T>>(name) {
    override fun predict(input: ParserContext): Boolean {
        return parser.predict(input)
    }

    override fun parse(input: ParserContext): Pair<ValueNode<T>, ParserContext> {
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
