package com.eden.monadik

import strikt.api.Assertion

fun Assertion<Pair<Node, ParserContext>?>.parsedCorrectly(expected: String, allowRemaining: Boolean = false): Assertion<Pair<Node, ParserContext>?> =
        assert("parsedCorrectly") {
            if(subject == null) fail(
                    message = "Subject cannot be null",
                    actual = subject
            )
            else {
                when(subject!!.first.toString()) {
                    expected.trimIndent().trim() -> pass()
                    else                         -> fail(
                            message = "Output AST should be %s",
                            actual = subject!!.first
                    )
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
        }

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
