package com.eden.monadik

class ChoiceNode(val node: Node, name: String, context: NodeContext) : Node(name, context) {
    override fun printAst(currentIndent: Int): String {
        return "${indent(currentIndent)}(ChoiceNode$nodeName:\n" +
                node.printAst(currentIndent + 2) +
                "\n" +
                "${indent(currentIndent)})"
    }

    override val children: List<Node>?
        get() = listOf(node)
}

class ChoiceParser(private vararg val parsers: Parser, name: String = "") : Parser(name) {
    override fun parse(input: ParserContext): Pair<Node, ParserContext> {
        for (parser in parsers) {
            val next = parser.test(input)
            if (next != null) {
                return Pair(ChoiceNode(next.first, name, NodeContext(input, next.second)), next.second)
            }
        }

        throw ParserException("No inputs matched", this, input)
    }
}
