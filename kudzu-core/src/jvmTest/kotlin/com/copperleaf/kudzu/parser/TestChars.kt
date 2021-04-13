package com.copperleaf.kudzu.parser

import com.copperleaf.kudzu.Node
import com.copperleaf.kudzu.ParserContext
import com.copperleaf.kudzu.parsedCorrectly
import com.copperleaf.kudzu.parsedIncorrectly
import kotlin.test.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isFalse
import strikt.assertions.isTrue

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

        expectThat(output).parsedCorrectly(expected)
        expectThat(output!!.first.text).isEqualTo("a")
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()
    }

    @Test
    fun testEscapedCharParser() {
        var input: String
        var output: Pair<Node, ParserContext>?
        var expected: String
        val underTest = CharParser(escapeChar = '\\')

        input = """\a"""
        output = underTest.test(input)
        expected = """(CharNode: 'a')"""

        expectThat(output).parsedCorrectly(expected)
        expectThat(output!!.first.text).isEqualTo("a")
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = """\"""
        output = underTest.test(input)
        expectThat(output).parsedIncorrectly()
        expectThat(underTest.predict(ParserContext(input, 0, false))).isFalse()
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
        expectThat(output).parsedCorrectly(expected)
        expectThat(output!!.first.text).isEqualTo("a")
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = "b"
        output = underTest.test(input)
        expected = """(CharNode: 'b')"""
        expectThat(output).parsedCorrectly(expected)
        expectThat(output!!.first.text).isEqualTo("b")
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = "c"
        output = underTest.test(input)
        expectThat(output).parsedIncorrectly()
        expectThat(underTest.predict(ParserContext(input, 0, false))).isFalse()
    }

    @Test
    fun testEscapedCharInParser() {
        var input: String
        var output: Pair<Node, ParserContext>?
        var expected: String
        val underTest = CharInParser('a', 'b', escapeChar = '\\')

        input = """\a"""
        output = underTest.test(input)
        expected = """(CharNode: 'a')"""
        expectThat(output).parsedCorrectly(expected)
        expectThat(output!!.first.text).isEqualTo("a")
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = """\b"""
        output = underTest.test(input)
        expected = """(CharNode: 'b')"""
        expectThat(output).parsedCorrectly(expected)
        expectThat(output!!.first.text).isEqualTo("b")
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = """\c"""
        output = underTest.test(input)
        expectThat(output).parsedIncorrectly()
        expectThat(underTest.predict(ParserContext(input, 0, false))).isFalse()

        input = """\"""
        output = underTest.test(input)
        expectThat(output).parsedIncorrectly()
        expectThat(underTest.predict(ParserContext(input, 0, false))).isFalse()
    }

    @Test
    fun testCharNotInParser() {
        var input: String
        var output: Pair<Node, ParserContext>?
        var expected: String
        val underTest = CharNotInParser('a', 'b')

        input = "a"
        output = underTest.test(input)
        expectThat(output).parsedIncorrectly()
        expectThat(underTest.predict(ParserContext(input, 0, false))).isFalse()

        input = "b"
        output = underTest.test(input)
        expectThat(output).parsedIncorrectly()
        expectThat(underTest.predict(ParserContext(input, 0, false))).isFalse()

        input = "c"
        expected = """(CharNode: 'c')"""
        output = underTest.test(input)
        expectThat(output).parsedCorrectly(expected)
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()
    }

    @Test
    fun testEscapedCharNotInParser() {
        var input: String
        var output: Pair<Node, ParserContext>?
        val underTest = CharNotInParser('a', 'b', escapeChar = '\\')

        input = """a"""
        output = underTest.test(input)
        expectThat(output).parsedIncorrectly()
        expectThat(underTest.predict(ParserContext(input, 0, false))).isFalse()

        input = """\a"""
        output = underTest.test(input)
        expectThat(output).parsedCorrectly()
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = """b"""
        output = underTest.test(input)
        expectThat(output).parsedIncorrectly()
        expectThat(underTest.predict(ParserContext(input, 0, false))).isFalse()

        input = """\b"""
        output = underTest.test(input)
        expectThat(output).parsedCorrectly()
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = """c"""
        output = underTest.test(input)
        expectThat(output).parsedCorrectly()
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = """\c"""
        output = underTest.test(input)
        expectThat(output).parsedCorrectly()
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = """\"""
        output = underTest.test(input)
        expectThat(output).parsedIncorrectly()
        expectThat(underTest.predict(ParserContext(input, 0, false))).isFalse()
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
        expectThat(output).parsedCorrectly(expected)
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = "\n"
        output = underTest.test(input)
        expected = "(CharNode: '\n')"
        expectThat(output).parsedCorrectly(expected)
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = "\t"
        output = underTest.test(input)
        expected = "(CharNode: '\t')"
        expectThat(output).parsedCorrectly(expected)
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = "a"
        output = underTest.test(input)
        expectThat(output).parsedIncorrectly()
        expectThat(underTest.predict(ParserContext(input, 0, false))).isFalse()
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
        expectThat(output).parsedCorrectly(expected)
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = "1"
        output = underTest.test(input)
        expected = "(CharNode: '1')"
        expectThat(output).parsedCorrectly(expected)
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = "a"
        output = underTest.test(input)
        expectThat(output).parsedIncorrectly()
        expectThat(underTest.predict(ParserContext(input, 0, false))).isFalse()
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
        expectThat(output).parsedCorrectly(expected)
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = "b"
        output = underTest.test(input)
        expected = "(CharNode: 'b')"
        expectThat(output).parsedCorrectly(expected)
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = "1"
        output = underTest.test(input)
        expectThat(output).parsedIncorrectly()
        expectThat(underTest.predict(ParserContext(input, 0, false))).isFalse()
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

        expectThat(output).parsedCorrectly(expected)
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()
    }
}
