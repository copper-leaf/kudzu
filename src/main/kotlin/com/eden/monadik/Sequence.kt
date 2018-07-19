package com.eden.monadik

class SequenceNode(val nodeList: List<Node>, name: String, context: NodeContext) : Node(name, context) {
    override fun printAst(currentIndent: Int): String {
        return "${indent(currentIndent)}(SequenceNode$nodeName:\n" +
                nodeList.map { it.printAst(currentIndent + 2) }.joinToString(separator = "\n") +
                "\n" +
                "${indent(currentIndent)})"
    }

    override val children: List<Node>?
        get() = nodeList
}

class SequenceParser(private vararg val parsers: Parser, name: String = "") : Parser(name) {
    override fun parse(input: ParserContext): Pair<Node, ParserContext> {
        val nodeList = ArrayList<Node>()

        var remaining = input
        var next: Pair<Node, ParserContext>?
        for (parser in parsers) {
            next = parser.parse(remaining)
            nodeList.add(next.first)
            remaining = next.second
        }

        return Pair(SequenceNode(nodeList, name, NodeContext(input, remaining)), remaining)
    }
}
