package com.copperleaf.kudzu.parser.text

import com.copperleaf.kudzu.checkParsingWhenEmpty
import com.copperleaf.kudzu.expectThat
import com.copperleaf.kudzu.get
import com.copperleaf.kudzu.isEqualTo
import com.copperleaf.kudzu.isFalse
import com.copperleaf.kudzu.isNotNull
import com.copperleaf.kudzu.isTrue
import com.copperleaf.kudzu.node
import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.parsedCorrectly
import com.copperleaf.kudzu.parsedIncorrectly
import com.copperleaf.kudzu.parser.ParserContext
import com.copperleaf.kudzu.parser.ParserResult
import com.copperleaf.kudzu.remainingText
import com.copperleaf.kudzu.test
import kotlin.test.Test

@OptIn(ExperimentalStdlibApi::class)
class TestWord {

    @Test
    fun testLiteralTokenParser() {
        val underTest = LiteralTokenParser("kotlin")

        expectThat(underTest.test("kotlin"))
            .parsedCorrectly(
                """
                |(TextNode: 'kotlin')
                """.trimMargin()
            )
            .node()
            .isNotNull()
            .get { text }
            .isEqualTo("kotlin")
        expectThat(underTest.predict(ParserContext.fromString("kotlin"))).isTrue()

        expectThat(underTest.test("kotli"))
            .parsedIncorrectly()
        expectThat(underTest.predict(ParserContext.fromString("kotli"))).isFalse()

        expectThat(underTest.test("kotlin yo"))
            .parsedCorrectly(
                """
                |(TextNode: 'kotlin')
                """.trimMargin(),
                allowRemaining = true
            )
            .also {
                it.node()
                    .isNotNull()
                    .get { text }
                    .isEqualTo("kotlin")

                it.remainingText()
                    .isEqualTo(" yo")
            }
        expectThat(underTest.predict(ParserContext.fromString("kotlin yo"))).isTrue()

        underTest.checkParsingWhenEmpty()
    }

    @Test
    fun testAnyTokenParser() {
        var input: String
        var output: ParserResult<Node>?
        val underTest = AnyTokenParser()

        input = "kotlin"
        output = underTest.test(input)
        expectThat(output)
            .parsedCorrectly()
            .node()
            .isNotNull()
            .get { text }
            .isEqualTo("kotlin")
        expectThat(underTest.predict(ParserContext.fromString(input))).isTrue()

        input = "java"
        output = underTest.test(input)
        expectThat(output)
            .parsedCorrectly()
            .node()
            .isNotNull()
            .get { text }
            .isEqualTo("java")
        expectThat(underTest.predict(ParserContext.fromString(input))).isTrue()

        input = "12345"
        output = underTest.test(input)
        expectThat(output)
            .parsedCorrectly()
            .node()
            .isNotNull()
            .get { text }
            .isEqualTo("12345")
        expectThat(underTest.predict(ParserContext.fromString(input))).isTrue()

        input = "="
        output = underTest.test(input)
        expectThat(output).parsedIncorrectly()
        expectThat(underTest.predict(ParserContext.fromString(input))).isFalse()

        input = " "
        output = underTest.test(input)
        expectThat(output).parsedIncorrectly()
        expectThat(underTest.predict(ParserContext.fromString(input))).isFalse()

        input = ""
        output = underTest.test(input)
        expectThat(output).parsedIncorrectly()
        expectThat(underTest.predict(ParserContext.fromString(input))).isFalse()

        underTest.checkParsingWhenEmpty()
    }
}
