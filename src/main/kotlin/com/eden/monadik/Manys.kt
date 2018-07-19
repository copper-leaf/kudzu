package com.eden.monadik

class ManyNode(private val nodeList: List<Node>, name: String, context: NodeContext) : Node(name, context) {
    override fun printAst(currentIndent: Int): String {
        return "${indent(currentIndent)}(ManyNode$nodeName:\n" +
                nodeList.map { it.printAst(currentIndent + 2) }.joinToString(separator = "\n") +
                "\n" +
                "${indent(currentIndent)})"
    }

    override val children: List<Node>?
        get() = nodeList
}

class ManyParser(private val parser: Parser, name: String = "") : Parser(name) {
    override fun parse(input: ParserContext): Pair<Node, ParserContext> {
        val nodeList = ArrayList<Node>()

        var remaining = input
        var next: Pair<Node, ParserContext>?
        while (remaining.isNotEmpty()) {
            next = parser.test(remaining)
            if (next != null) {
                nodeList.add(next.first)
                remaining = next.second
            }
            else {
                break
            }
        }

        return Pair(ManyNode(nodeList, name, NodeContext(input, remaining)), remaining)
    }
}

class AtLeastParser(private val minSize: Int, private val parser: Parser, name: String = "") : Parser(name) {
    override fun parse(input: ParserContext): Pair<Node, ParserContext> {
        val nodeList = ArrayList<Node>()

        var remaining = input
        var next: Pair<Node, ParserContext>?
        while (remaining.isNotEmpty()) {
            next = parser.test(remaining)
            if (next != null) {
                nodeList.add(next.first)
                remaining = next.second
            }
            else {
                break
            }
        }

        if (nodeList.size < minSize) throw ParserException("must have at least $minSize ${if (minSize == 1) "match" else "matches"}", this, input)

        return Pair(ManyNode(nodeList, name, NodeContext(input, remaining)), remaining)
    }
}

class AtMostParser(private val maxSize: Int, private val parser: Parser, name: String = "") : Parser(name) {
    override fun parse(input: ParserContext): Pair<Node, ParserContext> {
        val nodeList = ArrayList<Node>()

        var remaining = input
        var next: Pair<Node, ParserContext>?
        while (remaining.isNotEmpty()) {
            if (nodeList.size > maxSize) throw ParserException("must have at most $maxSize ${if (maxSize == 1) "match" else "matches"}", this, input)

            next = parser.test(remaining)
            if (next != null) {
                nodeList.add(next.first)
                remaining = next.second
            }
            else {
                break
            }
        }

        return Pair(ManyNode(nodeList, name, NodeContext(input, remaining)), remaining)
    }
}