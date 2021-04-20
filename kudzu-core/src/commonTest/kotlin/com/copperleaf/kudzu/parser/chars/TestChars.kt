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
import kotlin.test.Test

@OptIn(ExperimentalStdlibApi::class)
class TestChars {

    @Test
    fun testCharParser() {
        var input: String
        var output: ParserResult<Node>?
        var expected: String
        val underTest = AnyCharParser()

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
        var output: ParserResult<Node>?
        var expected: String
        val underTest = AnyCharParser()

        input = """\a"""
        output = underTest.test(input)
        expected = """(CharNode: '\')"""

        expectThat(output).parsedCorrectly(expected, allowRemaining = true)
        expectThat(output!!.first.text).isEqualTo("\\")
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = """\"""
        output = underTest.test(input)
        expectThat(output).parsedCorrectly()
        expectThat(output!!.first.text).isEqualTo("\\")
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()
    }

    @Test
    fun testCharInParser() {
        var input: String
        var output: ParserResult<Node>?
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
        var output: ParserResult<Node>?
        val underTest = CharInParser('a', 'b')

        input = """\a"""
        output = underTest.test(input)
        expectThat(output).parsedIncorrectly()
        expectThat(underTest.predict(ParserContext(input, 0, false))).isFalse()

        input = """a"""
        output = underTest.test(input)
        expectThat(output).parsedCorrectly("(CharNode: 'a')")
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = """\b"""
        output = underTest.test(input)
        expectThat(output).parsedIncorrectly()
        expectThat(underTest.predict(ParserContext(input, 0, false))).isFalse()

        input = """b"""
        output = underTest.test(input)
        expectThat(output).parsedCorrectly("(CharNode: 'b')")
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
        var output: ParserResult<Node>?
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
        var output: ParserResult<Node>?
        val underTest = CharNotInParser('a', 'b')

        input = """a"""
        output = underTest.test(input)
        expectThat(output).parsedIncorrectly()
        expectThat(underTest.predict(ParserContext(input, 0, false))).isFalse()

        input = """\a"""
        output = underTest.test(input)
        expectThat(output).parsedCorrectly(allowRemaining = true)
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = """b"""
        output = underTest.test(input)
        expectThat(output).parsedIncorrectly()
        expectThat(underTest.predict(ParserContext(input, 0, false))).isFalse()

        input = """\b"""
        output = underTest.test(input)
        expectThat(output).parsedCorrectly(allowRemaining = true)
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = """c"""
        output = underTest.test(input)
        expectThat(output).parsedCorrectly()
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = """\c"""
        output = underTest.test(input)
        expectThat(output).parsedCorrectly(allowRemaining = true)
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = """\"""
        output = underTest.test(input)
        expectThat(output).parsedCorrectly()
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()
    }

    @Test
    fun testWhitespaceParser() {
        var input: String
        var output: ParserResult<Node>?
        var expected: String
        val underTest = WhitespaceCharParser()

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
        var output: ParserResult<Node>?
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
        var output: ParserResult<Node>?
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
        var output: ParserResult<Node>?
        var expected: String
        val underTest = AnyCharParser()

        input = "a"
        output = underTest.test(input)
        expected = """(CharNode: 'a')"""

        expectThat(output).parsedCorrectly(expected)
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()
    }
}
