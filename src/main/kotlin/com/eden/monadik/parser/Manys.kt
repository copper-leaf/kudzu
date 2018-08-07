package com.eden.monadik.parser

import com.eden.monadik.Node
import com.eden.monadik.NodeContext
import com.eden.monadik.NonTerminalNode
import com.eden.monadik.Parser
import com.eden.monadik.ParserContext
import com.eden.monadik.ParserException

class ManyNode(private val nodeList: List<Node>, name: String, context: NodeContext) : NonTerminalNode(name, context) {
    override val children: List<Node> get() = nodeList
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
            if (nodeList.size == maxSize) break

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

class TimesParser(private val times: Int, private val parser: Parser, name: String = "") : Parser(name) {
    override fun parse(input: ParserContext): Pair<Node, ParserContext> {
        val nodeList = ArrayList<Node>()

        var remaining = input
        var next: Pair<Node, ParserContext>?
        for (i in 0 until times) {
            next = parser.parse(remaining)
            nodeList.add(next.first)
            remaining = next.second
        }

        return Pair(ManyNode(nodeList, name, NodeContext(input, remaining)), remaining)
    }
}

class BetweenTimesParser(private val minSize: Int, private val maxSize: Int, private val parser: Parser, name: String = "") : Parser(name) {
    override fun parse(input: ParserContext): Pair<Node, ParserContext> {
        val nodeList = ArrayList<Node>()

        var remaining = input
        var next: Pair<Node, ParserContext>?
        while (remaining.isNotEmpty()) {
            if (nodeList.size == maxSize) break

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