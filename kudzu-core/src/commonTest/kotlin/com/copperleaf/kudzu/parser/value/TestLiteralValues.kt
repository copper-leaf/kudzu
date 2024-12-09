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

class TestLiteralValues {

    val startNumberValue = -100
    val endNumberValue = 100

    @Test
    fun testNullLiteralParser() {
        val nullTests = listOf(
            "null" to { null },
            "NULL" to null,
        )

        NullLiteralParser().runTests(nullTests)
        AnyLiteralParser().runTests(nullTests, allTestsShouldFail = true)
        AnyNullableLiteralParser().runTests(nullTests)
    }

    @Test
    fun testBooleanLiteralParser() {
        val booleanTests = listOf(
            "true" to { true },
            "false" to { false },
            "TRUE" to null,
            "FALSE" to null,
        )

        BooleanLiteralParser().runTests(booleanTests)
        AnyLiteralParser().runTests(booleanTests)
        AnyNullableLiteralParser().runTests(booleanTests)
    }

    @Test
    fun testDoubleLiteralParser() {
        val doubleTests = sequence {
            val random = Random(1)

            var value = startNumberValue.toDouble()
            while (value <= endNumberValue) {
                value += random.nextDouble(0.1, 1.0)
                val currentValue = value
                yield("$value" to { currentValue })
            }

            yield("-100.0" to { -100.0 })
            yield("-100.00" to { -100.0 })
            yield("0.0" to { 0.0 })
            yield("-0.0" to { -0.0 })
            yield("100.00" to { 100.0 })
            yield("100.0" to { 100.0 })
        }.toList()

        val intTests = sequence {
            yield("-100" to null)
            yield("0" to null)
            yield("100" to null)
        }.toList()

        DoubleLiteralParser().runTests(doubleTests + intTests)
        AnyLiteralParser().runTests(doubleTests)
        AnyNullableLiteralParser().runTests(doubleTests)
    }

    @Test
    fun testIntLiteralParser() {
        val intTests = (startNumberValue..endNumberValue).map { "$it" to { it } }

        IntLiteralParser().runTests(intTests)
        AnyLiteralParser().runTests(intTests)
        AnyNullableLiteralParser().runTests(intTests)
    }

    @Test
    fun testStringLiteralParser() {
        val stringTests = listOf(
            """  "a"      """ to { "a" },
            """  "0123asdf"      """ to { "0123asdf" },
            """  "\\\r\n\t"      """ to { "\\\r\n\t" },
            """  "\'asdf\'"      """ to { "'asdf'" },
            """  "\"asdf\""      """ to { "\"asdf\"" },
            """  "\u00A2\u00b5"  """ to { "¢µ" },
            """  "\uFDFD"        """ to { "﷽" },
            """  "0123asdf \u00A2 \\ \r \'asdf\' \n \t \u00b5 \"asdf\""  """ to {
                "0123asdf ¢ \\ \r 'asdf' \n \t µ \"asdf\""
            },
        ).map { it.first.trim() to it.second }

        StringLiteralParser().runTests(stringTests)
        AnyLiteralParser().runTests(stringTests)
        AnyNullableLiteralParser().runTests(stringTests)
    }

    @Test
    fun testCharLiteralParser() {
        val charTests = listOf(
            """  '0'       """ to { '0' },
            """  '1'       """ to { '1' },
            """  '2'       """ to { '2' },
            """  '3'       """ to { '3' },
            """  '4'       """ to { '4' },
            """  '5'       """ to { '5' },
            """  '6'       """ to { '6' },
            """  '7'       """ to { '7' },
            """  '8'       """ to { '8' },
            """  '9'       """ to { '9' },
            """  'a'       """ to { 'a' },
            """  'A'       """ to { 'A' },
            """  'b'       """ to { 'b' },
            """  'B'       """ to { 'B' },
            """  'c'       """ to { 'c' },
            """  'C'       """ to { 'C' },
            """  'd'       """ to { 'd' },
            """  'D'       """ to { 'D' },
            """  'e'       """ to { 'e' },
            """  'E'       """ to { 'E' },
            """  'f'       """ to { 'f' },
            """  'F'       """ to { 'F' },
            """  '\\'      """ to { '\\' },
            """  '\r'      """ to { '\r' },
            """  '\n'      """ to { '\n' },
            """  '\t'      """ to { '\t' },
            """  '\''      """ to { '\'' },
            """  '\"'      """ to { '"' },
            """  '\u00A2'  """ to { '¢' },
            """  '\u00b5'  """ to { 'µ' },
        ).map { it.first.trim() to it.second }

        CharLiteralParser().runTests(charTests)
        AnyLiteralParser().runTests(charTests)
        AnyNullableLiteralParser().runTests(charTests)
    }
}

private fun <T> Parser<ValueNode<T>>.runTests(
    tests: List<Pair<String, (() -> T?)?>>,
    allTestsShouldFail: Boolean = false,
    logErrors: Boolean = false
) {
    val underTest = this
    tests.forEach { (input, expectedValueFn) ->
        val expectedValue = expectedValueFn?.invoke()

        if (logErrors) {
            println("input=$input, expectedValue=$expectedValue")
        }

        if (expectedValueFn == null) {
            expectThat(underTest.test(input)).parsedIncorrectly()
        } else if (allTestsShouldFail) {
            expectThat(underTest.test(input)).parsedIncorrectly()
        } else {
            expectThat(underTest.test(input, logErrors = logErrors))
                .parsedCorrectly()
                .node()
                .isNotNull()
                .apply {
                    text.isEqualTo("$expectedValue")
                    value.isEqualTo(expectedValue)
                }
        }
    }
}
