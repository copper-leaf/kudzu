package com.copperleaf.kudzu.parser.text

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
import com.copperleaf.kudzu.test
import kotlin.test.Test

class TestWhitespace {

    @Test
    fun testOptionalWhitespaceParser() {
        var input: String
        var output: ParserResult<Node>?
        val underTest = OptionalWhitespaceParser()

        input = ""
        output = underTest.test(input)
        expectThat(output).parsedCorrectly()
        expectThat(underTest.predict(ParserContext.fromString(input))).isTrue()

        input = " "
        output = underTest.test(input)
        expectThat(output)
            .parsedCorrectly()
            .node()
            .isNotNull()
            .get { text }
            .isEqualTo(" ")
        expectThat(underTest.predict(ParserContext.fromString(input))).isTrue()

        input = "\n"
        output = underTest.test(input)
        expectThat(output).parsedCorrectly()
        expectThat(underTest.predict(ParserContext.fromString(input))).isTrue()

        input = "     "
        output = underTest.test(input)
        expectThat(output).parsedCorrectly()
        expectThat(underTest.predict(ParserContext.fromString(input))).isTrue()

        input = " \n \n\n\n"
        output = underTest.test(input)
        expectThat(output).parsedCorrectly()
        expectThat(underTest.predict(ParserContext.fromString(input))).isTrue()

        input = " asdf "
        output = underTest.test(input)
        expectThat(output).parsedIncorrectly()
        expectThat(underTest.predict(ParserContext.fromString(input))).isTrue()

        input = "asdf "
        output = underTest.test(input)
        expectThat(output).parsedIncorrectly()
        expectThat(underTest.predict(ParserContext.fromString(input))).isFalse()

        expectThat(underTest.test("")).parsedCorrectly()
        expectThat(underTest.predict(ParserContext.fromString(""))).isTrue()
    }

    @Test
    fun testRequiredWhitespaceParser() {
        var input: String
        var output: ParserResult<Node>?
        val underTest = RequiredWhitespaceParser()

        input = ""
        output = underTest.test(input)
        expectThat(output).parsedIncorrectly()
        expectThat(underTest.predict(ParserContext.fromString(input))).isFalse()

        input = " "
        output = underTest.test(input)
        expectThat(output).parsedCorrectly()
        expectThat(underTest.predict(ParserContext.fromString(input))).isTrue()

        input = "\n"
        output = underTest.test(input)
        expectThat(output).parsedCorrectly()
        expectThat(underTest.predict(ParserContext.fromString(input))).isTrue()

        input = "     "
        output = underTest.test(input)
        expectThat(output).parsedCorrectly()
        expectThat(underTest.predict(ParserContext.fromString(input))).isTrue()

        input = " \n \n\n\n"
        output = underTest.test(input)
        expectThat(output).parsedCorrectly()
        expectThat(underTest.predict(ParserContext.fromString(input))).isTrue()

        input = " asdf "
        output = underTest.test(input)
        expectThat(output).parsedIncorrectly()
        expectThat(underTest.predict(ParserContext.fromString(input))).isTrue()

        input = "asdf "
        output = underTest.test(input)
        expectThat(output).parsedIncorrectly()
        expectThat(underTest.predict(ParserContext.fromString(input))).isFalse()

        expectThat(underTest.test("")).parsedIncorrectly()
        expectThat(underTest.predict(ParserContext.fromString(""))).isFalse()
    }
}
