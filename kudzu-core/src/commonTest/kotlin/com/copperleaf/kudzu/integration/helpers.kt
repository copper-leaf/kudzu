package com.copperleaf.kudzu.integration

import com.copperleaf.kudzu.Node
import com.copperleaf.kudzu.ParserContext
import kotlin.test.assertEquals

fun Pair<Node, ParserContext>?.parsedCorrectly(
    expected: String? = null,
    allowRemaining: Boolean = false
): Pair<Node, ParserContext> {
    if (this == null) error(
        "Subject cannot be null"
    )
    else {
        if (expected != null) {
            when (first.toString()) {
                expected.trimIndent().trim() -> {
                }
                else -> error(
                    "Output AST should be $first"
                )
            }
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

fun Pair<Node, ParserContext>?.node(): Node? = this?.first

fun <T> expectThat(value: T): T {
    return value
}

fun <T> List<T>.containsExactlyInAnyOrder(vararg items: T) {
    check(this == listOf(*items))
}

fun <T, U> T.get(block: T.() -> U): U {
    return block()
}

fun <T> T.isEqualTo(other: Any?) {
    assertEquals(other, this)
}

fun Boolean.isFalse() {
    assertEquals(false, this)
}

fun <T> T?.isNotNull(): T {
    return checkNotNull(this)
}

fun Boolean.isTrue() {
    assertEquals(true, this)
}
