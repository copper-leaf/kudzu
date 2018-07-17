package com.eden.monadik

class ManyNode(private val nodeList: List<Node>) : Node() {
    override fun printAst(currentIndent: Int): String {
        return "${indent(currentIndent)}(ManyNode:\n" +
                nodeList.map { it.printAst(currentIndent + 2) }.joinToString(separator = "\n") +
                "\n" +
                "${indent(currentIndent)})"
    }
}

class ManyParser(private val parser: Parser) : Parser() {
    override fun parse(input: ParsingContext): Pair<Node, ParsingContext> {
        val nodeList = ArrayList<Node>()

        var remaining = input
        var next: Pair<Node, ParsingContext>?
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

        return Pair(ManyNode(nodeList), remaining)
    }
}

class AtLeastParser(private val minSize: Int, private val parser: Parser) : Parser() {
    override fun parse(input: ParsingContext): Pair<Node, ParsingContext> {
        val nodeList = ArrayList<Node>()

        var remaining = input
        var next: Pair<Node, ParsingContext>?
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

        return Pair(ManyNode(nodeList), remaining)
    }
}

class AtMostParser(private val maxSize: Int, private val parser: Parser) : Parser() {
    override fun parse(input: ParsingContext): Pair<Node, ParsingContext> {
        val nodeList = ArrayList<Node>()

        var remaining = input
        var next: Pair<Node, ParsingContext>?
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

        return Pair(ManyNode(nodeList), remaining)
    }
}