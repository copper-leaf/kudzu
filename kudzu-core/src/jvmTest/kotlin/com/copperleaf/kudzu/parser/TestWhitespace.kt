package com.copperleaf.kudzu.parser

import com.copperleaf.kudzu.Node
import com.copperleaf.kudzu.ParserContext
import com.copperleaf.kudzu.checkParsingWhenEmpty
import com.copperleaf.kudzu.node
import com.copperleaf.kudzu.parsedCorrectly
import com.copperleaf.kudzu.parsedIncorrectly
import kotlin.test.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isFalse
import strikt.assertions.isNotNull
import strikt.assertions.isTrue

class TestWhitespace {

    @Test
    fun testOptionalWhitespaceParser() {
        var input: String
        var output: Pair<Node, ParserContext>?
        val underTest = OptionalWhitespaceParser()

        input = ""
        output = underTest.test(input)
        expectThat(output).parsedCorrectly()
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = " "
        output = underTest.test(input)
        expectThat(output)
            .parsedCorrectly()
            .node()
            .isNotNull()
            .get { text }
            .isEqualTo("")
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = "\n"
        output = underTest.test(input)
        expectThat(output).parsedCorrectly()
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = "     "
        output = underTest.test(input)
        expectThat(output).parsedCorrectly()
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = " \n \n\n\n"
        output = underTest.test(input)
        expectThat(output).parsedCorrectly()
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = " asdf "
        output = underTest.test(input)
        expectThat(output).parsedIncorrectly()
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = "asdf "
        output = underTest.test(input)
        expectThat(output).parsedIncorrectly()
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()

        underTest.checkParsingWhenEmpty(true)
    }

    @Test
    fun testRequiredWhitespaceParser() {
        var input: String
        var output: Pair<Node, ParserContext>?
        val underTest = RequiredWhitespaceParser()

        input = ""
        output = underTest.test(input)
        expectThat(output).parsedIncorrectly()
        expectThat(underTest.predict(ParserContext(input, 0, false))).isFalse()

        input = " "
        output = underTest.test(input)
        expectThat(output).parsedCorrectly()
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = "\n"
        output = underTest.test(input)
        expectThat(output).parsedCorrectly()
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = "     "
        output = underTest.test(input)
        expectThat(output).parsedCorrectly()
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = " \n \n\n\n"
        output = underTest.test(input)
        expectThat(output).parsedCorrectly()
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = " asdf "
        output = underTest.test(input)
        expectThat(output).parsedIncorrectly()
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = "asdf "
        output = underTest.test(input)
        expectThat(output).parsedIncorrectly()
        expectThat(underTest.predict(ParserContext(input, 0, false))).isFalse()

        underTest.checkParsingWhenEmpty()
    }
}
