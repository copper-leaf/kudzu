package com.copperleaf.kudzu.parser

import com.copperleaf.kudzu.Node
import com.copperleaf.kudzu.ParserContext
import com.copperleaf.kudzu.node
import com.copperleaf.kudzu.parsedCorrectly
import com.copperleaf.kudzu.parsedIncorrectly
import org.junit.jupiter.api.Test
import strikt.api.expect
import strikt.assertions.isEqualTo
import strikt.assertions.isFalse
import strikt.assertions.isTrue

class TestWhitespace {

    @Test
    fun testOptionalWhitespaceParser() {
        var input: String
        var output: Pair<Node, ParserContext>?
        val underTest = OptionalWhitespaceParser()

        input = ""
        output = underTest.test(input)
        expect(output).parsedCorrectly()
        expect(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = " "
        output = underTest.test(input)
        expect(output)
                .parsedCorrectly()
                .node()
                .map { this!!.text }
                .isEqualTo("")
        expect(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = "\n"
        output = underTest.test(input)
        expect(output).parsedCorrectly()
        expect(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = "     "
        output = underTest.test(input)
        expect(output).parsedCorrectly()
        expect(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = " \n \n\n\n"
        output = underTest.test(input)
        expect(output).parsedCorrectly()
        expect(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = " asdf "
        output = underTest.test(input)
        expect(output).parsedIncorrectly()
        expect(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = "asdf "
        output = underTest.test(input)
        expect(output).parsedIncorrectly()
        expect(underTest.predict(ParserContext(input, 0, false))).isTrue()
    }

    @Test
    fun testRequiredWhitespaceParser() {
        var input: String
        var output: Pair<Node, ParserContext>?
        val underTest = RequiredWhitespaceParser()

        input = ""
        output = underTest.test(input)
        expect(output).parsedIncorrectly()
        expect(underTest.predict(ParserContext(input, 0, false))).isFalse()

        input = " "
        output = underTest.test(input)
        expect(output).parsedCorrectly()
        expect(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = "\n"
        output = underTest.test(input)
        expect(output).parsedCorrectly()
        expect(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = "     "
        output = underTest.test(input)
        expect(output).parsedCorrectly()
        expect(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = " \n \n\n\n"
        output = underTest.test(input)
        expect(output).parsedCorrectly()
        expect(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = " asdf "
        output = underTest.test(input)
        expect(output).parsedIncorrectly()
        expect(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = "asdf "
        output = underTest.test(input)
        expect(output).parsedIncorrectly()
        expect(underTest.predict(ParserContext(input, 0, false))).isFalse()
    }
}
