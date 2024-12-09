package com.copperleaf.kudzu.parser

import com.copperleaf.kudzu.checkNotEmpty
import com.copperleaf.kudzu.expectCatching
import com.copperleaf.kudzu.expectThat
import com.copperleaf.kudzu.get
import com.copperleaf.kudzu.isEqualTo
import com.copperleaf.kudzu.isFailure
import com.copperleaf.kudzu.isFalse
import com.copperleaf.kudzu.isSuccess
import com.copperleaf.kudzu.isTrue
import com.copperleaf.kudzu.parser.chars.AnyCharParser
import kotlin.test.Test

class TestHelpersMethods {

    @Test
    fun testIsEmpty() {
        val underTest = ParserContext.fromString("")

        expectThat(underTest.isEmpty()).isTrue()

        expectCatching { AnyCharParser().checkNotEmpty(underTest) }
            .isFailure()
            .get { message }
            .isEqualTo(
                """
                |Parse error at 1:1 (AnyCharParser)
                |
                |unexpected end of input
                |
                |1|
                |>>^
                """.trimMargin()
            )
    }

    @Test
    fun testIsNotEmpty() {
        val underTest = ParserContext.fromString("2 + 3")

        expectThat(underTest.isEmpty()).isFalse()

        expectCatching { AnyCharParser().checkNotEmpty(underTest) }
            .isSuccess()

        AnyCharParser().checkNotEmpty(underTest)
    }
}
