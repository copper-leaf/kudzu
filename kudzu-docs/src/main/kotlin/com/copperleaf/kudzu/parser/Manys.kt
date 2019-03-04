package com.copperleaf.kudzu.parser

import com.copperleaf.kudzu.Node
import com.copperleaf.kudzu.NodeContext
import com.copperleaf.kudzu.NonTerminalNode
import com.copperleaf.kudzu.Parser
import com.copperleaf.kudzu.ParserContext
import com.copperleaf.kudzu.ParserException
import com.copperleaf.kudzu.checkNotEmpty

class ManyNode(private val nodeList: List<Node>, name: String, context: NodeContext) : NonTerminalNode(name, context) {
    override val children: List<Node> get() = nodeList
}

abstract class BaseManyParser(
    protected val parser: Parser, name: String = "") : Parser(name) {
    override fun predict(input: ParserContext): Boolean {
        return input.isNotEmpty() && parser.predict(input)
    }
}

/**
 * Consume input as many times as its parser is able to.
 *
 * Predicts true when:
 *   - there is remaining input
 *   - its parser predicts true
 *
 * Parsing stops when:
 *   - its parser predicts false
 *   - there is no more input remaining
 *
 * Parsing fails when:
 *   - its parser fails to parse
 */
class ManyParser(parser: Parser, name: String = "") : BaseManyParser(parser, name) {
    override fun parse(input: ParserContext): Pair<Node, ParserContext> {
        checkNotEmpty(input)

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

/**
 * Consume input as many times as its parser is able to, but it must be able to parse a `minSize` number of times.
 *
 * Predicts true when:
 *   - there is remaining input
 *   - its parser predicts true
 *
 * Parsing stops when:
 *   - its parser predicts false
 *   - there is no more input remaining
 *
 * Parsing fails when:
 *   - its parser fails to parse
 *   - its parser could not parse at least `minSize` number of times.
 */
class AtLeastParser(private val minSize: Int, parser: Parser, name: String = "") : BaseManyParser(parser, name) {
    override fun parse(input: ParserContext): Pair<Node, ParserContext> {
        checkNotEmpty(input)

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

/**
 * Consume input from its parser up to `maxSize` number of times.
 *
 * Predicts true when:
 *   - there is remaining input
 *   - its parser predicts true
 *
 * Parsing stops when:
 *   - its parser predicts false
 *   - there is no more input remaining
 *   - we have parsed `maxSize` number of times already
 *
 * Parsing fails when:
 *   - its parser fails to parse
 */
class AtMostParser(private val maxSize: Int, parser: Parser, name: String = "") : BaseManyParser(parser, name) {
    override fun parse(input: ParserContext): Pair<Node, ParserContext> {
        checkNotEmpty(input)

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

/**
 * Consume input from its parser a specific number of times.
 *
 * Predicts true when:
 *   - there is remaining input
 *   - its parser predicts true
 *
 * Parsing stops when:
 *   - we have parsed `times` number of times already
 *
 * Parsing fails when:
 *   - its parser fails to parse
 *   - there is no more input remaining
 */
class TimesParser(private val times: Int, parser: Parser, name: String = "") : BaseManyParser(parser, name) {
    override fun parse(input: ParserContext): Pair<Node, ParserContext> {
        checkNotEmpty(input)

        val nodeList = ArrayList<Node>()

        var remaining = input
        var next: Pair<Node, ParserContext>?
        for (i in 0 until times) {
            if(!parser.predict(remaining)) throw ParserException("unexpected end of input after $i iterations, expected $times", this, remaining)
            next = parser.parse(remaining)
            nodeList.add(next.first)
            remaining = next.second
        }

        return Pair(ManyNode(nodeList, name, NodeContext(input, remaining)), remaining)
    }
}

/**
 * Consume input from its parser between `minSize` and `maxSize` number of times.
 *
 * Predicts true when:
 *   - there is remaining input
 *   - its parser predicts true
 *
 * Parsing stops when:
 *   - we have parsed `maxSize` number of times already
 *
 * Parsing fails when:
 *   - its parser fails to parse
 *   - its parser could not parse at least `minSize` number of times.
 */
class BetweenTimesParser(private val minSize: Int, private val maxSize: Int, parser: Parser, name: String = "") : BaseManyParser(parser, name) {
    override fun parse(input: ParserContext): Pair<Node, ParserContext> {
        checkNotEmpty(input)

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

/**
 * Consume input as many times as its parser is able to, or until a stopping condition is reached.
 *
 * Predicts true when:
 *   - there is remaining input
 *   - its parser predicts true
 *
 * Parsing stops when:
 *   - its parser predicts false
 *   - the stopping condition predicts true
 *   - there is no more input remaining
 *
 * Parsing fails when:
 *   - its parser fails to parse
 */
class UntilParser(parser: Parser, private val stoppingCondition: Parser, name: String = "") : BaseManyParser(parser, name) {
    override fun parse(input: ParserContext): Pair<Node, ParserContext> {
        checkNotEmpty(input)

        val nodeList = ArrayList<Node>()

        var remaining = input
        var next: Pair<Node, ParserContext>?
        while (remaining.isNotEmpty()) {
            if (parser.predict(remaining) && !stoppingCondition.predict(remaining)) {
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