package com.copperleaf.kudzu.parser

import com.copperleaf.kudzu.ParserContext
import com.copperleaf.kudzu.checkNotEmpty
import kotlin.test.Test
import strikt.api.expectCatching
import strikt.api.expectThat
import strikt.assertions.*

class TestHelpersMethods {

    @Test
    fun testIsEmpty() {
        val underTest = ParserContext("", 0, false)

        expectThat(underTest.isEmpty()).isTrue()
        expectThat(underTest.isNotEmpty()).isFalse()

        expectCatching { CharParser().checkNotEmpty(underTest) }
            .isFailure()
            .get { message }
            .isEqualTo("Parse error: unexpected end of input (CharParser() at 1:1)")
    }

    @Test
    fun testIsNotEmpty() {
        val underTest = ParserContext("2 + 3", 0, false)

        expectThat(underTest.isEmpty()).isFalse()
        expectThat(underTest.isNotEmpty()).isTrue()

        expectCatching { CharParser().checkNotEmpty(underTest) }
            .isSuccess()

        CharParser().checkNotEmpty(underTest)
    }
}
