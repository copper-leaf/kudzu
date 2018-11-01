package com.copperleaf.kudzu

import strikt.api.Assertion

fun Assertion.Builder<Pair<Node, ParserContext>?>.parsedCorrectly(expected: String? = null, allowRemaining: Boolean = false): Assertion.Builder<Pair<Node, ParserContext>> =
        assert("parsedCorrectly") {
            if(it == null) fail(
                    description = "Subject cannot be null",
                    actual = it
            )
            else {
                if(expected != null) {
                    when(it.first.toString()) {
                        expected.trimIndent().trim() -> pass()
                        else                         -> fail(
                                description = "Output AST should be %s",
                                actual = it.first
                        )
                    }
                }
                if(!allowRemaining) {
                    when (it.second.isEmpty()) {
                        true -> pass()
                        else -> fail(
                                description = "There should be nothing remaining, still had %s",
                                actual = it.second
                        )
                    }
                }
            }
        }.get { this!! }

fun Assertion.Builder<Pair<Node, ParserContext>?>.parsedIncorrectly(): Assertion.Builder<Pair<Node, ParserContext>?> =
        assert("parsedIncorrectly") {
            if(it == null) pass()
            else when(it.second.isNotEmpty()) {
                true -> pass()
                else -> fail(
                        description = "Subject must be null or have input remaining",
                        actual = it.second
                )
            }
        }

fun Assertion.Builder<Pair<Node, ParserContext>>.node(): Assertion.Builder<Node?> = get { first }
fun Assertion.Builder<Pair<Node, ParserContext>>.context(): Assertion.Builder<ParserContext?> = get { second }
fun Assertion.Builder<Pair<Node, ParserContext>>.thenLog(): Assertion.Builder<Pair<Node, ParserContext>> {
    this.node().get { println(toString()) }
    return this
}

fun Assertion.Builder<Node?>.isTerminal(): Assertion.Builder<TerminalNode> =
        assert("isTerminal") {
            if(it == null) fail(
                    description = "Subject must be non-null",
                    actual = it
            )
            if(it is TerminalNode) pass()
            else fail(
                    description = "Subject must be be an instance of TerminalNode",
                    actual = it
            )
        }.get { this!! as TerminalNode }

fun Assertion.Builder<Node?>.isNonTerminal(): Assertion.Builder<NonTerminalNode> =
        assert("isTerminal") {
            if(it == null) fail(
                    description = "Subject must be non-null",
                    actual = it
            )
            if(it is NonTerminalNode) pass()
            else fail(
                    description = "Subject must be be an instance of NonTerminalNode",
                    actual = it
            )
        }.get { this!! as NonTerminalNode }

fun Assertion.Builder<NonTerminalNode>.withChildren(expectedChildrenCount: Int): Assertion.Builder<NonTerminalNode> =
        assert("isTerminal") {
            when(it.children.size) {
                expectedChildrenCount -> pass()
                else -> fail(
                        description = "Subject should have have $expectedChildrenCount children",
                        actual = it.children.size
                )
            }
        }
