package com.copperleaf.kudzu.parser

import com.copperleaf.kudzu.Node
import com.copperleaf.kudzu.NodeContext
import com.copperleaf.kudzu.NonTerminalNode
import com.copperleaf.kudzu.Parser
import com.copperleaf.kudzu.ParserContext
import com.copperleaf.kudzu.ParserException

class ManyNode(private val nodeList: List<Node>, name: String, context: NodeContext) : NonTerminalNode(name, context) {
    override val children: List<Node> get() = nodeList
}

abstract class BaseManyParser(protected val parser: Parser, name: String = "") : Parser(name) {
    override fun predict(input: ParserContext): Boolean {
        return parser.predict(input)
    }
}

class ManyParser(parser: Parser, name: String = "") : BaseManyParser(parser, name) {
    override fun parse(input: ParserContext): Pair<Node, ParserContext> {
        val nodeList = ArrayList<Node>()

        var remaining = input
        var next: Pair<Node, ParserContext>?
        while (remaining.isNotEmpty()) {
            if (parser.predict(remaining)) {
                next = parser.parse(remaining)
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

class AtLeastParser(private val minSize: Int, parser: Parser, name: String = "") : BaseManyParser(parser, name) {
    override fun parse(input: ParserContext): Pair<Node, ParserContext> {
        val nodeList = ArrayList<Node>()

        var remaining = input
        var next: Pair<Node, ParserContext>?
        while (remaining.isNotEmpty()) {
            if (parser.predict(remaining)) {
                next = parser.parse(remaining)
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

class AtMostParser(private val maxSize: Int, parser: Parser, name: String = "") : BaseManyParser(parser, name) {
    override fun parse(input: ParserContext): Pair<Node, ParserContext> {
        val nodeList = ArrayList<Node>()

        var remaining = input
        var next: Pair<Node, ParserContext>?
        while (remaining.isNotEmpty()) {
            if (nodeList.size == maxSize) break

            if (parser.predict(remaining)) {
                next = parser.parse(remaining)
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

class TimesParser(private val times: Int, parser: Parser, name: String = "") : BaseManyParser(parser, name) {
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

class BetweenTimesParser(private val minSize: Int, private val maxSize: Int, parser: Parser, name: String = "") : BaseManyParser(parser, name) {
    override fun parse(input: ParserContext): Pair<Node, ParserContext> {
        val nodeList = ArrayList<Node>()

        var remaining = input
        var next: Pair<Node, ParserContext>?
        while (remaining.isNotEmpty()) {
            if (nodeList.size == maxSize) break

            if (parser.predict(remaining)) {
                next = parser.parse(remaining)
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