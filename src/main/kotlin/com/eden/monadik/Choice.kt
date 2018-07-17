package com.eden.monadik

class ChoiceNode(val node: Node) : Node() {
    override fun printAst(currentIndent: Int): String {
        return "${indent(currentIndent)}(ChoiceNode:\n" +
                node.printAst(currentIndent + 2) +
                "\n" +
                "${indent(currentIndent)})"
    }
}

class ChoiceParser(private vararg val parsers: Parser) : Parser() {
    override fun parse(input: ParsingContext): Pair<Node, ParsingContext> {
        for (parser in parsers) {
            val next = parser.test(input)
            if (next != null) {
                return Pair(ChoiceNode(next.first), next.second)
            }
        }

        throw ParserException("No inputs matched", this, input)
    }
}
