package com.copperleaf.kudzu.parser.chars

import com.copperleaf.kudzu.expectThat
import com.copperleaf.kudzu.isEqualTo
import com.copperleaf.kudzu.isFalse
import com.copperleaf.kudzu.isTrue
import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.parsedCorrectly
import com.copperleaf.kudzu.parsedIncorrectly
import com.copperleaf.kudzu.parser.ParserContext
import com.copperleaf.kudzu.parser.ParserResult
import com.copperleaf.kudzu.test
import io.kotest.core.spec.style.StringSpec

class TestNewline : StringSpec({

    "testNewlineParserValid_unixStyle" {
        var input: String
        var output: ParserResult<Node>?
        var expected: String
        val underTest = NewlineCharParser()

        input = "\n"
        output = underTest.test(input)
        expected = """(NewlineNode: '\n')"""

        expectThat(output).parsedCorrectly(expected)
        expectThat(output!!.first.text).isEqualTo("\\n")
        expectThat(underTest.predict(ParserContext.fromString(input))).isTrue()
    }

    "testNewlineParserValid_windowsStyle" {
        var input: String
        var output: ParserResult<Node>?
        var expected: String
        val underTest = NewlineCharParser()

        input = "\r\n"
        output = underTest.test(input)
        expected = """(NewlineNode: '\n')"""

        expectThat(output).parsedCorrectly(expected)
        expectThat(output!!.first.text).isEqualTo("\\n")
        expectThat(underTest.predict(ParserContext.fromString(input))).isTrue()
    }

    "testNewlineParserFails_crOnly" {
        var input: String
        var output: ParserResult<Node>?
        val underTest = NewlineCharParser()

        input = "\r"
        output = underTest.test(
            input,
            expectedErrorMessage = """
            |Parse error at 1:1 (NewlineCharParser)
            |
            |\r must be followed by \n
            |
            |1|
            |>>^
            |2|
            """.trimMargin()
        )

        expectThat(output).parsedIncorrectly()
        expectThat(underTest.predict(ParserContext.fromString(input))).isTrue()
    }

    "testNewlineParserFails_crNotFollowedByLf" {
        var input: String
        var output: ParserResult<Node>?
        val underTest = NewlineCharParser()

        input = "\ra"
        output = underTest.test(
            input,
            expectedErrorMessage = """
            |Parse error at 1:1 (NewlineCharParser)
            |
            |\r must be followed by \n
            |
            |1|
            |>>^
            |2|a
            """.trimMargin()
        )

        expectThat(output).parsedIncorrectly()
        expectThat(underTest.predict(ParserContext.fromString(input))).isTrue()
    }

    "testNewlineParserFails_notCrOrLf" {
        var input: String
        var output: ParserResult<Node>?
        val underTest = NewlineCharParser()

        input = "a"
        output = underTest.test(
            input,
            expectedErrorMessage = """
            |Parse error at 1:1 (NewlineCharParser)
            |
            |Expected '\n' or '\r\n', got 'a'
            |
            |1|a
            |>>^
            """.trimMargin()
        )

        expectThat(output).parsedIncorrectly()
        expectThat(underTest.predict(ParserContext.fromString(input))).isFalse()
    }
})
