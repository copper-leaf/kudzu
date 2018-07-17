package com.eden.monadik

class MaybeNode(val node: Node?) : Node() {
    override fun printAst(currentIndent: Int): String {
        if (node != null) {
            return "${indent(currentIndent)}(MaybeNode:\n" +
                    node.printAst(currentIndent + 2) +
                    "\n" +
                    "${indent(currentIndent)})"
        }
        else {
            return "${indent(currentIndent)}(MaybeNode: (empty))"
        }
    }
}

class MaybeParser(val parser: Parser) : Parser() {
    override fun parse(input: ParsingContext): Pair<Node, ParsingContext> {
        val next = parser.test(input)
        if (next != null) {
            return Pair(MaybeNode(next.first), next.second)
        }
        else {
            return Pair(MaybeNode(null), input)
        }
    }
}