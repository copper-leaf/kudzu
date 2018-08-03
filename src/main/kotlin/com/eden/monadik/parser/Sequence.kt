package com.eden.monadik.parser

import com.eden.monadik.Node
import com.eden.monadik.NodeContext
import com.eden.monadik.NonTerminalNode
import com.eden.monadik.Parser
import com.eden.monadik.ParserContext

class SequenceNode(val nodeList: List<Node>, name: String, context: NodeContext) : NonTerminalNode(name, context) {
    override fun printAst(currentIndent: Int): String {
        return "${indent(currentIndent)}(SequenceNode$nodeName:\n" +
                nodeList.map { it.printAst(currentIndent + 2) }.joinToString(separator = "\n") +
                "\n" +
                "${indent(currentIndent)})"
    }

    override val children: List<Node>
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
