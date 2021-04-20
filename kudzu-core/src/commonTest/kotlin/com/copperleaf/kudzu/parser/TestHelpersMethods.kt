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

@OptIn(ExperimentalStdlibApi::class)
class TestHelpersMethods {

    @Test
    fun testIsEmpty() {
        val underTest = ParserContext("", 0, false)

        expectThat(underTest.isEmpty()).isTrue()
        expectThat(underTest.isNotEmpty()).isFalse()

        expectCatching { AnyCharParser().checkNotEmpty(underTest) }
            .isFailure()
            .get { message }
            .isEqualTo("Parse error: unexpected end of input (AnyCharParser at 1:1)")
    }

    @Test
    fun testIsNotEmpty() {
        val underTest = ParserContext("2 + 3", 0, false)

        expectThat(underTest.isEmpty()).isFalse()
        expectThat(underTest.isNotEmpty()).isTrue()

        expectCatching { AnyCharParser().checkNotEmpty(underTest) }
            .isSuccess()

        AnyCharParser().checkNotEmpty(underTest)
    }
}
