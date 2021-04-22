package com.copperleaf.kudzu.parser.value

import com.copperleaf.kudzu.expectThat
import com.copperleaf.kudzu.isEqualTo
import com.copperleaf.kudzu.isNotNull
import com.copperleaf.kudzu.node
import com.copperleaf.kudzu.node.mapped.ValueNode
import com.copperleaf.kudzu.parsedCorrectly
import com.copperleaf.kudzu.parsedIncorrectly
import com.copperleaf.kudzu.parser.Parser
import com.copperleaf.kudzu.test
import kotlin.random.Random
import kotlin.test.Test

@OptIn(ExperimentalStdlibApi::class)
class TestLiteralValues {

    private val startNumberValue = -100
    private val endNumberValue = 100

    private val nullTests = listOf(
        "null" to { null },
        "NULL" to null,
    )
    private val booleanTests = listOf(
        "true" to { true },
        "false" to { false },
        "TRUE" to null,
        "FALSE" to null,
    )
    private val intTests = (startNumberValue..endNumberValue).map { "$it" to { it } }
    private val doubleTests = sequence {
        val random = Random(1)

        var value = startNumberValue.toDouble()
        do {
            val currentValue = value
            yield("$value" to { currentValue })
            value += random.nextDouble(0.1, 1.0)
        } while (value <= endNumberValue)

        yield("${startNumberValue.toDouble()}" to { startNumberValue.toDouble() })
        yield("0.0" to { 0.0 })
        yield("-0.0" to { -0.0 })
        yield("${endNumberValue.toDouble()}" to { endNumberValue.toDouble() })
    }.toList()

    @Test
    fun testNullLiteralParser() {
        NullLiteralParser().runTests(nullTests)
        AnyLiteralParser().runTests(nullTests, allTestsShouldFail = true)
        AnyNullableLiteralParser().runTests(nullTests)
    }

    @Test
    fun testBooleanLiteralParser() {
        BooleanLiteralParser().runTests(booleanTests)
        AnyLiteralParser().runTests(booleanTests)
        AnyNullableLiteralParser().runTests(booleanTests)
    }

    @Test
    fun testIntLiteralParser() {
        IntLiteralParser().runTests(intTests)
        AnyLiteralParser().runTests(intTests)
        AnyNullableLiteralParser().runTests(intTests)
    }

    @Test
    fun testDoubleLiteralParser() {
        DoubleLiteralParser().runTests(doubleTests)
        AnyLiteralParser().runTests(doubleTests)
        AnyNullableLiteralParser().runTests(doubleTests)
    }

    private fun <T> Parser<ValueNode<T>>.runTests(
        tests: List<Pair<String, (() -> T?)?>>,
        allTestsShouldFail: Boolean = false
    ) {
        val underTest = this
        tests.forEach { (input, expectedValueFn) ->
            val expectedValue = expectedValueFn?.invoke()

            println("input=$input, expectedValue=$expectedValue")

            if (expectedValueFn != null && !allTestsShouldFail) {
                expectThat(underTest.test(input))
                    .parsedCorrectly(
                        """
                        |(ValueNode: '$expectedValue')
                        """.trimMargin()
                    )
                    .node()
                    .isNotNull()
                    .apply {
                        text.isEqualTo("$expectedValue")
                        value.isEqualTo(expectedValue)
                    }
            } else {
                expectThat(underTest.test(input)).parsedIncorrectly()
            }
        }
    }
}
