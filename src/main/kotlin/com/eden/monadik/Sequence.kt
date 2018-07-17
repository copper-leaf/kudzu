package com.eden.monadik

class SequenceNode(val nodeList: List<Node>) : Node() {
    override fun printAst(currentIndent: Int): String {
        return "${indent(currentIndent)}(SequenceNode:\n" +
                nodeList.map { it.printAst(currentIndent + 2) }.joinToString(separator = "\n") +
                "\n" +
                "${indent(currentIndent)})"
    }
}

class SequenceParser(private vararg val parsers: Parser) : Parser() {
    override fun parse(input: ParsingContext): Pair<Node, ParsingContext> {
        val nodeList = ArrayList<Node>()

        var remaining = input
        var next: Pair<Node, ParsingContext>?
        for (parser in parsers) {
            next = parser.parse(remaining)
            nodeList.add(next.first)
            remaining = next.second
        }

        return Pair(SequenceNode(nodeList), remaining)
    }
}
