package com.eden.monadik.parser

import com.eden.monadik.Node
import com.eden.monadik.ParserContext
import com.eden.monadik.parsedCorrectly
import com.eden.monadik.parsedIncorrectly
import org.junit.jupiter.api.Test
import strikt.api.expect

class TestWhitespace {

    @Test
    fun testOptionalWhitespaceParser() {
        var input: String
        var output: Pair<Node, ParserContext>?
        val underTest = OptionalWhitespaceParser()

        input = ""
        output = underTest.test(input)
        expect(output).parsedCorrectly()

        input = " "
        output = underTest.test(input)
        expect(output).parsedCorrectly()

        input = "\n"
        output = underTest.test(input)
        expect(output).parsedCorrectly()

        input = "     "
        output = underTest.test(input)
        expect(output).parsedCorrectly()

        input = " \n \n\n\n"
        output = underTest.test(input)
        expect(output).parsedCorrectly()

        input = " asdf "
        output = underTest.test(input)
        expect(output).parsedIncorrectly()
    }

    @Test
    fun testRequiredWhitespaceParser() {
        var input: String
        var output: Pair<Node, ParserContext>?
        val underTest = RequiredWhitespaceParser()

        input = ""
        output = underTest.test(input)
        expect(output).parsedIncorrectly()

        input = " "
        output = underTest.test(input)
        expect(output).parsedCorrectly()

        input = "\n"
        output = underTest.test(input)
        expect(output).parsedCorrectly()

        input = "     "
        output = underTest.test(input)
        expect(output).parsedCorrectly()

        input = " \n \n\n\n"
        output = underTest.test(input)
        expect(output).parsedCorrectly()

        input = " asdf "
        output = underTest.test(input)
        expect(output).parsedIncorrectly()
    }
}
