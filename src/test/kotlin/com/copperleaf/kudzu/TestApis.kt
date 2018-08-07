package com.copperleaf.kudzu

import strikt.api.Assertion

fun Assertion<Pair<Node, ParserContext>?>.parsedCorrectly(expected: String? = null, allowRemaining: Boolean = false): Assertion<Pair<Node, ParserContext>> =
        assert("parsedCorrectly") {
            if(subject == null) fail(
                    message = "Subject cannot be null",
                    actual = subject
            )
            else {
                if(expected != null) {
                    when(subject!!.first.toString()) {
                        expected.trimIndent().trim() -> pass()
                        else                         -> fail(
                                message = "Output AST should be %s",
                                actual = subject!!.first
                        )
                    }
                }
                if(!allowRemaining) {
                    when (subject!!.second.isEmpty()) {
                        true -> pass()
                        else -> fail(
                                message = "There should be nothing remaining, still had %s",
                                actual = subject!!.second
                        )
                    }
                }
            }
        }.map { this!! }

fun Assertion<Pair<Node, ParserContext>?>.parsedIncorrectly(): Assertion<Pair<Node, ParserContext>?> =
        assert("parsedIncorrectly") {
            if(subject == null) pass()
            else when(subject!!.second.isNotEmpty()) {
                true -> pass()
                else -> fail(
                        message = "Subject must be null or have input remaining",
                        actual = subject!!.second
                )
            }
        }

fun Assertion<Pair<Node, ParserContext>>.node(): Assertion<Node?> = map { this.first }
fun Assertion<Pair<Node, ParserContext>>.context(): Assertion<ParserContext?> = map { this.second }

fun Assertion<Node?>.isTerminal(): Assertion<TerminalNode> =
        assert("isTerminal") {
            if(subject == null) fail(
                    message = "Subject must be non-null",
                    actual = subject
            )
            if(subject is TerminalNode) pass()
            else fail(
                    message = "Subject must be be an instance of TerminalNode",
                    actual = subject
            )
        }.map { this!! as TerminalNode }

fun Assertion<Node?>.isNonTerminal(): Assertion<NonTerminalNode> =
        assert("isTerminal") {
            if(subject == null) fail(
                    message = "Subject must be non-null",
                    actual = subject
            )
            if(subject is NonTerminalNode) pass()
            else fail(
                    message = "Subject must be be an instance of NonTerminalNode",
                    actual = subject
            )
        }.map { this!! as NonTerminalNode }

fun Assertion<NonTerminalNode>.withChildren(expectedChildrenCount: Int): Assertion<NonTerminalNode> =
        assert("isTerminal") {
            when(subject.children.size) {
                expectedChildrenCount -> pass()
                else -> fail(
                        message = "Subject should have have $expectedChildrenCount children",
                        actual = subject.children.size
                )
            }
        }
