package com.eden.monadik

import com.eden.monadik.parser.CharInParser
import com.eden.monadik.parser.CharNotInParser
import com.eden.monadik.parser.CharParser
import com.eden.monadik.parser.DigitParser
import com.eden.monadik.parser.LetterParser
import com.eden.monadik.parser.WhitespaceParser
import org.junit.jupiter.api.Test
import strikt.api.expect
import strikt.assertions.isNull

class TestChars {

    @Test
    fun testCharParser() {
        var input: String
        var output: Pair<Node, ParserContext>?
        var expected: String
        val underTest = CharParser()

        input = "a"
        output = underTest.test(input)
        expected = """(CharNode: 'a')"""

        expect(output).parsedCorrectly(expected)
        expect(output!!.first.children).isNull() // terminals have no children
    }

    @Test
    fun testCharInParser() {
        var input: String
        var output: Pair<Node, ParserContext>?
        var expected: String
        val underTest = CharInParser('a', 'b')

        input = "a"
        output = underTest.test(input)
        expected = """(CharNode: 'a')"""
        expect(output).parsedCorrectly(expected)

        input = "b"
        output = underTest.test(input)
        expected = """(CharNode: 'b')"""
        expect(output).parsedCorrectly(expected)

        input = "c"
        output = underTest.test(input)
        expect(output).parsedIncorrectly()
    }

    @Test
    fun testCharNotInParser() {
        var input: String
        var output: Pair<Node, ParserContext>?
        var expected: String
        val underTest = CharNotInParser('a', 'b')

        input = "a"
        output = underTest.test(input)
        expect(output).parsedIncorrectly()

        input = "b"
        output = underTest.test(input)
        expect(output).parsedIncorrectly()

        input = "c"
        expected = """(CharNode: 'c')"""
        output = underTest.test(input)
        expect(output).parsedCorrectly(expected)
    }

    @Test
    fun testWhitespaceParser() {
        var input: String
        var output: Pair<Node, ParserContext>?
        var expected: String
        val underTest = WhitespaceParser()

        input = " "
        output = underTest.test(input)
        expected = "(CharNode: ' ')"
        expect(output).parsedCorrectly(expected)

        input = "\n"
        output = underTest.test(input)
        expected = "(CharNode: '\n')"
        expect(output).parsedCorrectly(expected)

        input = "\t"
        output = underTest.test(input)
        expected = "(CharNode: '\t')"
        expect(output).parsedCorrectly(expected)

        input = "a"
        output = underTest.test(input)
        expect(output).parsedIncorrectly()
    }

    @Test
    fun testDigitParser() {
        var input: String
        var output: Pair<Node, ParserContext>?
        var expected: String
        val underTest = DigitParser()

        input = "0"
        output = underTest.test(input)
        expected = "(CharNode: '0')"
        expect(output).parsedCorrectly(expected)

        input = "1"
        output = underTest.test(input)
        expected = "(CharNode: '1')"
        expect(output).parsedCorrectly(expected)

        input = "a"
        output = underTest.test(input)
        expect(output).parsedIncorrectly()
    }

    @Test
    fun testLetterParser() {
        var input: String
        var output: Pair<Node, ParserContext>?
        var expected: String
        val underTest = LetterParser()

        input = "a"
        output = underTest.test(input)
        expected = "(CharNode: 'a')"
        expect(output).parsedCorrectly(expected)

        input = "b"
        output = underTest.test(input)
        expected = "(CharNode: 'b')"
        expect(output).parsedCorrectly(expected)

        input = "1"
        output = underTest.test(input)
        expect(output).parsedIncorrectly()
    }

    @Test
    fun testNamedCharNode() {
        var input: String
        var output: Pair<Node, ParserContext>?
        var expected: String
        val underTest = CharParser(name = "charNode")

        input = "a"
        output = underTest.test(input)
        expected = """(CharNode:charNode: 'a')"""

        expect(output).parsedCorrectly(expected)
    }

}
