package com.eden.monadik

class MaybeNode(val node: Node?, name: String, context: NodeContext) : Node(name, context) {
    override fun printAst(currentIndent: Int): String {
        if (node != null) {
            return "${indent(currentIndent)}(MaybeNode$nodeName:\n" +
                    node.printAst(currentIndent + 2) +
                    "\n" +
                    "${indent(currentIndent)})"
        }
        else {
            return "${indent(currentIndent)}(MaybeNode$nodeName: (empty))"
        }
    }

    override val children: List<Node>?
        get() = if(node != null) listOf(node) else null
}

class MaybeParser(val parser: Parser, name: String = "") : Parser(name) {
    override fun parse(input: ParserContext): Pair<Node, ParserContext> {
        val next = parser.test(input)
        if (next != null) {
            return Pair(MaybeNode(next.first, name, NodeContext(input, next.second)), next.second)
        }
        else {
            return Pair(MaybeNode(null, name, NodeContext(input, input)), input)
        }
    }
}