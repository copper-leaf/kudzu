package com.copperleaf.kudzu.parser.chars

import com.copperleaf.kudzu.expectThat
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
import io.kotest.core.spec.style.StringSpec

class TestChars : StringSpec({

    "testCharParser" {
        var input: String
        var output: ParserResult<Node>?
        var expected: String
        val underTest = AnyCharParser()

        input = "a"
        output = underTest.test(input)
        expected = """(CharNode: 'a')"""

        expectThat(output).parsedCorrectly(expected)
        expectThat(output!!.first.text).isEqualTo("a")
        expectThat(underTest.predict(ParserContext.fromString(input))).isTrue()
    }

    "testEscapedAnyCharParser" {
        var input: String
        var output: ParserResult<Node>?
        var expected: String
        val underTest = AnyCharParser()

        input = """\a"""
        output = underTest.test(input)
        expected = """(CharNode: '\')"""

        expectThat(output).parsedCorrectly(expected, allowRemaining = true)
        expectThat(output!!.first.text).isEqualTo("\\")
        expectThat(underTest.predict(ParserContext.fromString(input))).isTrue()

        input = """\"""
        output = underTest.test(input)
        expectThat(output).parsedCorrectly()
        expectThat(output!!.first.text).isEqualTo("\\")
        expectThat(underTest.predict(ParserContext.fromString(input))).isTrue()
    }

    "testCharInParser" {
        var input: String
        var output: ParserResult<Node>?
        var expected: String
        val underTest = CharInParser('a', 'b')

        input = "a"
        output = underTest.test(input)
        expected = """(CharNode: 'a')"""
        expectThat(output).parsedCorrectly(expected)
        expectThat(output!!.first.text).isEqualTo("a")
        expectThat(underTest.predict(ParserContext.fromString(input))).isTrue()

        input = "b"
        output = underTest.test(input)
        expected = """(CharNode: 'b')"""
        expectThat(output).parsedCorrectly(expected)
        expectThat(output!!.first.text).isEqualTo("b")
        expectThat(underTest.predict(ParserContext.fromString(input))).isTrue()

        input = "c"
        output = underTest.test(input)
        expectThat(output).parsedIncorrectly()
        expectThat(underTest.predict(ParserContext.fromString(input))).isFalse()
    }

    "testEscapedCharInParser" {
        var input: String
        var output: ParserResult<Node>?
        val underTest = CharInParser('a', 'b')

        input = """\a"""
        output = underTest.test(input)
        expectThat(output).parsedIncorrectly()
        expectThat(underTest.predict(ParserContext.fromString(input))).isFalse()

        input = """a"""
        output = underTest.test(input)
        expectThat(output).parsedCorrectly("(CharNode: 'a')")
        expectThat(underTest.predict(ParserContext.fromString(input))).isTrue()

        input = """\b"""
        output = underTest.test(input)
        expectThat(output).parsedIncorrectly()
        expectThat(underTest.predict(ParserContext.fromString(input))).isFalse()

        input = """b"""
        output = underTest.test(input)
        expectThat(output).parsedCorrectly("(CharNode: 'b')")
        expectThat(underTest.predict(ParserContext.fromString(input))).isTrue()

        input = """\c"""
        output = underTest.test(input)
        expectThat(output).parsedIncorrectly()
        expectThat(underTest.predict(ParserContext.fromString(input))).isFalse()

        input = """\"""
        output = underTest.test(input)
        expectThat(output).parsedIncorrectly()
        expectThat(underTest.predict(ParserContext.fromString(input))).isFalse()
    }

    "testCharNotInParser" {
        var input: String
        var output: ParserResult<Node>?
        var expected: String
        val underTest = CharNotInParser('a', 'b')

        input = "a"
        output = underTest.test(input)
        expectThat(output).parsedIncorrectly()
        expectThat(underTest.predict(ParserContext.fromString(input))).isFalse()

        input = "b"
        output = underTest.test(input)
        expectThat(output).parsedIncorrectly()
        expectThat(underTest.predict(ParserContext.fromString(input))).isFalse()

        input = "c"
        expected = """(CharNode: 'c')"""
        output = underTest.test(input)
        expectThat(output).parsedCorrectly(expected)
        expectThat(underTest.predict(ParserContext.fromString(input))).isTrue()
    }

    "testEscapedCharNotInParser" {
        var input: String
        var output: ParserResult<Node>?
        val underTest = CharNotInParser('a', 'b')

        input = """a"""
        output = underTest.test(input)
        expectThat(output).parsedIncorrectly()
        expectThat(underTest.predict(ParserContext.fromString(input))).isFalse()

        input = """\a"""
        output = underTest.test(input)
        expectThat(output).parsedCorrectly(allowRemaining = true)
        expectThat(underTest.predict(ParserContext.fromString(input))).isTrue()

        input = """b"""
        output = underTest.test(input)
        expectThat(output).parsedIncorrectly()
        expectThat(underTest.predict(ParserContext.fromString(input))).isFalse()

        input = """\b"""
        output = underTest.test(input)
        expectThat(output).parsedCorrectly(allowRemaining = true)
        expectThat(underTest.predict(ParserContext.fromString(input))).isTrue()

        input = """c"""
        output = underTest.test(input)
        expectThat(output).parsedCorrectly()
        expectThat(underTest.predict(ParserContext.fromString(input))).isTrue()

        input = """\c"""
        output = underTest.test(input)
        expectThat(output).parsedCorrectly(allowRemaining = true)
        expectThat(underTest.predict(ParserContext.fromString(input))).isTrue()

        input = """\"""
        output = underTest.test(input)
        expectThat(output).parsedCorrectly()
        expectThat(underTest.predict(ParserContext.fromString(input))).isTrue()
    }

    "testWhitespaceParser" {
        var input: String
        var output: ParserResult<Node>?
        var expected: String
        val underTest = WhitespaceCharParser()

        input = " "
        output = underTest.test(input)
        expected = "(CharNode: ' ')"
        expectThat(output).parsedCorrectly(expected)
        expectThat(underTest.predict(ParserContext.fromString(input))).isTrue()

        input = "\n"
        output = underTest.test(input)
        expected = "(CharNode: '\n')"
        expectThat(output).parsedCorrectly(expected)
        expectThat(underTest.predict(ParserContext.fromString(input))).isTrue()

        input = "\t"
        output = underTest.test(input)
        expected = "(CharNode: '\t')"
        expectThat(output).parsedCorrectly(expected)
        expectThat(underTest.predict(ParserContext.fromString(input))).isTrue()

        input = "a"
        output = underTest.test(input)
        expectThat(output).parsedIncorrectly()
        expectThat(underTest.predict(ParserContext.fromString(input))).isFalse()
    }

    "testDigitParser" {
        var input: String
        var output: ParserResult<Node>?
        var expected: String
        val underTest = DigitParser()

        input = "0"
        output = underTest.test(input)
        expected = "(CharNode: '0')"
        expectThat(output).parsedCorrectly(expected)
        expectThat(underTest.predict(ParserContext.fromString(input))).isTrue()

        input = "1"
        output = underTest.test(input)
        expected = "(CharNode: '1')"
        expectThat(output).parsedCorrectly(expected)
        expectThat(underTest.predict(ParserContext.fromString(input))).isTrue()

        input = "a"
        output = underTest.test(input)
        expectThat(output).parsedIncorrectly()
        expectThat(underTest.predict(ParserContext.fromString(input))).isFalse()
    }

    "testLetterParser" {
        var input: String
        var output: ParserResult<Node>?
        var expected: String
        val underTest = LetterParser()

        input = "a"
        output = underTest.test(input)
        expected = "(CharNode: 'a')"
        expectThat(output).parsedCorrectly(expected)
        expectThat(underTest.predict(ParserContext.fromString(input))).isTrue()

        input = "b"
        output = underTest.test(input)
        expected = "(CharNode: 'b')"
        expectThat(output).parsedCorrectly(expected)
        expectThat(underTest.predict(com.copperleaf.kudzu.parser.ParserContext.fromString(input))).isTrue()

        input = "1"
        output = underTest.test(input)
        expectThat(output).parsedIncorrectly()
        expectThat(underTest.predict(com.copperleaf.kudzu.parser.ParserContext.fromString(input))).isFalse()
    }

    "testNamedCharNode" {
        var input: String
        var output: ParserResult<Node>?
        var expected: String
        val underTest = AnyCharParser()

        input = "a"
        output = underTest.test(input)
        expected = """(CharNode: 'a')"""

        expectThat(output).parsedCorrectly(expected)
        expectThat(underTest.predict(ParserContext.fromString(input))).isTrue()
    }

    "testHexDigitParser" {
        val underTest = HexDigitParser()

        listOf(
            "0" to '0',
            "1" to '1',
            "2" to '2',
            "3" to '3',
            "4" to '4',
            "5" to '5',
            "6" to '6',
            "7" to '7',
            "8" to '8',
            "9" to '9',
            "a" to 'a',
            "A" to 'A',
            "b" to 'b',
            "B" to 'B',
            "c" to 'c',
            "C" to 'C',
            "d" to 'd',
            "D" to 'D',
            "e" to 'e',
            "E" to 'E',
            "f" to 'f',
            "F" to 'F',
        ).forEach { (input, expectedValue) ->
            expectThat(underTest.test(input))
                .parsedCorrectly()
                .node()
                .isNotNull()
                .apply {
                    this.char.isEqualTo(expectedValue)
                }
        }
    }

    "testEscapedCharParser" {
        val underTest = EscapedCharParser()

        listOf(
            """\\""" to '\\',
            """\r""" to '\r',
            """\n""" to '\n',
            """\t""" to '\t',
            """\'""" to '\'',
            """\"""" to '"',
            """\u00A2""" to '¢',
            """\u00b5""" to 'µ',
        ).forEach { (input, expectedValue) ->
            expectThat(underTest.test(input))
                .parsedCorrectly()
                .node()
                .isNotNull()
                .apply {
                    this.char.isEqualTo(expectedValue)
                }
        }
    }
})
