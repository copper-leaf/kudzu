package com.copperleaf.kudzu.parser

import com.copperleaf.kudzu.ParserContext
import com.copperleaf.kudzu.checkNotEmpty
import org.junit.jupiter.api.Test
import strikt.api.catching
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isFalse
import strikt.assertions.isNotNull
import strikt.assertions.isNull
import strikt.assertions.isTrue

class TestHelpersMethods {

    @Test
    fun testIsEmpty() {
        val underTest = ParserContext("", 0, false)

        expectThat(underTest.isEmpty()).isTrue()
        expectThat(underTest.isNotEmpty()).isFalse()

        expectThat(catching {
            CharParser().checkNotEmpty(underTest)
        })
            .isNotNull()
            .get { message }
            .isEqualTo("Parse error: unexpected end of input (CharParser() at 1:1)")
    }

    @Test
    fun testIsNotEmpty() {
        val underTest = ParserContext("2 + 3", 0, false)

        expectThat(underTest.isEmpty()).isFalse()
        expectThat(underTest.isNotEmpty()).isTrue()

        expectThat(catching {
            CharParser().checkNotEmpty(underTest)
        }).isNull()

        CharParser().checkNotEmpty(underTest)
    }

}