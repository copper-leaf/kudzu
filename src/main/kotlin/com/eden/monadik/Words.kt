package com.eden.monadik

class WordNode(private val word: String) : Node() {
    override fun printAst(currentIndent: Int): String {
        return "${indent(currentIndent)}(WordNode: $word)"
    }
}

class WordParser(val expected: String) : Parser() {
    override fun parse(input: ParsingContext): Pair<Node, ParsingContext> {
        if (input.isEmpty()) throw ParserException("nothing to parse", this, input)
        var remaining = input
        for (i in 0 until expected.length) {
            val nextChar = remaining.next()

            if (nextChar == expected[i]) {
                remaining = remaining.remaining()
            }
            else {
                throw ParserException("expected " + expected, this, input)
            }
        }
        return Pair(WordNode(expected), remaining)
    }
}
