
package com.copperleaf.kudzu

import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.node.NonTerminalNode
import com.copperleaf.kudzu.node.TerminalNode
import com.copperleaf.kudzu.parser.Parser
import com.copperleaf.kudzu.parser.ParserContext
import com.copperleaf.kudzu.parser.ParserException
import com.copperleaf.kudzu.parser.ParserResult
import kotlin.test.assertEquals

fun <T : Node> ParserResult<T>?.parsedCorrectly(
    expected: String? = null,
    allowRemaining: Boolean = false
): ParserResult<T> {
    if (this == null) {
        error(
            "Subject cannot be null"
        )
    } else {
        if (expected != null) {
            expected.trimIndent().trim() shouldBe first.toString()
        }
        if (!allowRemaining) {
            when (second.isEmpty()) {
                true -> {
                }
                else -> error(
                    "There should be nothing remaining, still had $second",
                )
            }
        }
    }

    return this
}

fun ParserResult<Node>?.parsedIncorrectly(): ParserResult<Node>? {
    if (this == null) {
    } else {
        when (!second.isEmpty()) {
            true -> {
            }
            else -> error("Subject must be null or have input remaining. Actual: $second")
        }
    }

    return this
}

fun <T : Node> ParserResult<T>?.node(): T? = this?.first

fun ParserResult<*>?.remainingText(): String? {
    if (this == null) return null

    var remaining = this.second
    var token = ""

    while (!remaining.isEmpty()) {
        val nextCharResult = remaining.nextChar()
        token += nextCharResult.first
        remaining = nextCharResult.second
    }

    return token
}

fun <T> expectThat(value: T): T {
    return value
}

fun <T : Any> expectCatching(value: () -> T): Pair<T?, Throwable?> {
    try {
        val evaluated = value()
        return evaluated to null
    } catch (t: Throwable) {
        return null to t
    }
}

fun <T : Any> Pair<T?, Throwable?>.isFailure(): Throwable {
    if (second != null) {
    } else {
        error("Expected $this to be failure, but was success")
    }

    return second!!
}

fun <T : Any> Pair<T?, Throwable?>.isSuccess(): T {
    if (first != null) {
    } else {
        error("Expected $this to be success, but was failure")
    }

    return first!!
}

fun <T> List<T>.containsExactly(vararg items: T): List<T> {
    check(this == listOf(*items))

    return this
}

fun <T> List<T>.hasSize(size: Int): List<T> {
    size shouldBe this.size

    return this
}

fun <T, U> T.get(block: T.() -> U): U {
    return block()
}

fun <T> T.isEqualTo(other: Any?): T {
    other shouldBe this

    return this
}

infix fun <T> T.shouldBe(other: Any?): T {
    assertEquals(
        expected = other,
        actual = this,
    )

    return this
}

inline fun <reified T> Any.isA(): T {
    check(this is T)

    return this
}

fun <T> T.isSameInstanceAs(other: Any?): T {
    check(this === other)

    return this
}

fun Boolean.isFalse() {
    this shouldBe false
}

fun <T> T?.isNotNull(): T {
    return checkNotNull(this)
}

fun Boolean.isTrue() {
    this shouldBe true
}

fun Node?.isTerminal(): TerminalNode {
    if (this == null) {
        error("Subject must be non-null. Actual: $this")
    } else if (this !is TerminalNode) {
        error("Subject must be be an instance of TerminalNode. Actual: $this")
    }

    return this
}

fun Node?.isNonTerminal(): NonTerminalNode {
    if (this == null) {
        error("Subject must be non-null. Actual: $this")
    } else if (this !is NonTerminalNode) {
        error("Subject must be be an instance of NonTerminalNode. Actual: $this")
    }

    return this
}

fun NonTerminalNode.withChildren(expectedChildrenCount: Int): NonTerminalNode {
    when (children.size) {
        expectedChildrenCount -> {
        }
        else -> error(
            "Subject should have have $expectedChildrenCount children. Actual: ${children.size}"
        )
    }

    return this
}

fun <NodeType : Node> Parser<NodeType>.test(
    input: String,
    skipWhitespace: Boolean = false,
    logErrors: Boolean = false,
    expectedErrorMessage: String? = null,
): ParserResult<NodeType>? {
    return test(ParserContext.fromString(input, skipWhitespace), logErrors, expectedErrorMessage)
}

fun <NodeType : Node> Parser<NodeType>.test(
    input: ParserContext,
    logErrors: Boolean = false,
    expectedErrorMessage: String? = null,
): Pair<NodeType, ParserContext>? {
    return try {
        parse(input)
    } catch (e: ParserException) {
        if (expectedErrorMessage != null) {
            e.message shouldBe expectedErrorMessage
        }
        if (logErrors) {
            e.printStackTrace()
            println("Parsing failed for input: '$input'")
        }
        null
    }
}
