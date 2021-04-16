package com.copperleaf.kudzu.parser.sequence

import com.copperleaf.kudzu.*
import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.parser.ParserContext
import com.copperleaf.kudzu.parser.ParserResult
import com.copperleaf.kudzu.parser.chars.DigitParser
import com.copperleaf.kudzu.parser.chars.LetterParser
import com.copperleaf.kudzu.parser.chars.WhitespaceCharParser
import com.copperleaf.kudzu.parser.many.AtLeastParser
import kotlin.test.Test

class TestSequence {

    @Test
    fun testSequenceOfCharParser() {
        var input: String
        var output: ParserResult<Node>?
        var expected: String
        val underTest = SequenceParser(
            DigitParser(),
            DigitParser(),
            DigitParser(),
            DigitParser()
        )

        input = "1234"
        output = underTest.test(input)
        expected = """
            (SequenceNode:
              (CharNode: '1')
              (CharNode: '2')
              (CharNode: '3')
              (CharNode: '4')
            )
        """
        expectThat(output).parsedCorrectly(expected)
        expectThat(output).parsedCorrectly(expected)
            .node()
            .isNonTerminal()
            .withChildren(4)
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()
    }

    @Test
    fun testSequenceOfManysParser() {
        var input: String
        var output: ParserResult<Node>?
        var expected: String
        val underTest = SequenceParser(
            AtLeastParser(DigitParser(), minSize = 1),
            AtLeastParser(WhitespaceCharParser(), minSize = 1),
            AtLeastParser(LetterParser(), minSize = 1)
        )

        input = "1234 asdf"
        output = underTest.test(input)
        expected = """
            (SequenceNode:
              (ManyNode:
                (CharNode: '1')
                (CharNode: '2')
                (CharNode: '3')
                (CharNode: '4')
              )
              (ManyNode:
                (CharNode: ' ')
              )
              (ManyNode:
                (CharNode: 'a')
                (CharNode: 's')
                (CharNode: 'd')
                (CharNode: 'f')
              )
            )
        """
        expectThat(output).parsedCorrectly(expected)
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()
    }

    @Test
    fun testSequenceOfManysParserSkippingWhitespace() {
        var input: String
        var output: ParserResult<Node>?
        var expected: String
        val underTest = SequenceParser(
            AtLeastParser(DigitParser(), minSize = 1),
            AtLeastParser(LetterParser(), minSize = 1),
            AtLeastParser(DigitParser(), minSize = 1),
            AtLeastParser(LetterParser(), minSize = 1)
        )

        input = "1234 asdf 12345 qwerty"
        output = underTest.test(input, true)
        expected = """
            (SequenceNode:
              (ManyNode:
                (CharNode: '1')
                (CharNode: '2')
                (CharNode: '3')
                (CharNode: '4')
              )
              (ManyNode:
                (CharNode: 'a')
                (CharNode: 's')
                (CharNode: 'd')
                (CharNode: 'f')
              )
              (ManyNode:
                (CharNode: '1')
                (CharNode: '2')
                (CharNode: '3')
                (CharNode: '4')
                (CharNode: '5')
              )
              (ManyNode:
                (CharNode: 'q')
                (CharNode: 'w')
                (CharNode: 'e')
                (CharNode: 'r')
                (CharNode: 't')
                (CharNode: 'y')
              )
            )
        """
        expectThat(output).parsedCorrectly(expected)
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()
    }

    @Test
    fun testSequenceCannotGetNext() {
        var input: String
        var output: ParserResult<Node>?
        val underTest = SequenceParser(
            AtLeastParser(DigitParser(), minSize = 1),
            AtLeastParser(LetterParser(), minSize = 1),
            AtLeastParser(DigitParser(), minSize = 1),
            AtLeastParser(LetterParser(), minSize = 1)
        )

        input = "1234 asdf 12345"
        output = underTest.test(input, true)
        expectThat(output).parsedIncorrectly()
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()
    }

    @Test
    fun testNamedSequenceNode() {
        var input: String
        var output: ParserResult<Node>?
        var expected: String
        val underTest = SequenceParser(
            DigitParser(),
            DigitParser(),
            DigitParser(),
            DigitParser(),
        )

        input = "1234"
        output = underTest.test(input)
        expected = """
            (SequenceNode:
              (CharNode: '1')
              (CharNode: '2')
              (CharNode: '3')
              (CharNode: '4')
            )
        """
        expectThat(output).parsedCorrectly(expected)
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()
    }
}
