package com.eden.monadik

import com.eden.monadik.parser.AtLeastParser
import com.eden.monadik.parser.DigitParser
import com.eden.monadik.parser.LetterParser
import com.eden.monadik.parser.SequenceParser
import com.eden.monadik.parser.WhitespaceParser
import org.junit.jupiter.api.Test
import strikt.api.expect
import strikt.assertions.hasSize
import strikt.assertions.isNotNull

class TestSequence {

    @Test
    fun testSequenceOfCharParser() {
        var input: String
        var output: Pair<Node, ParserContext>?
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
        expect(output).parsedCorrectly(expected)
        expect(output!!.first.children).isNotNull().hasSize(4)
    }

    @Test
    fun testSequenceOfManysParser() {
        var input: String
        var output: Pair<Node, ParserContext>?
        var expected: String
        val underTest = SequenceParser(
                AtLeastParser(1, DigitParser()),
                AtLeastParser(1, WhitespaceParser()),
                AtLeastParser(1, LetterParser())
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
        expect(output).parsedCorrectly(expected)
    }

    @Test
    fun testSequenceOfManysParserSkippingWhitespace() {
        var input: String
        var output: Pair<Node, ParserContext>?
        var expected: String
        val underTest = SequenceParser(
                AtLeastParser(1, DigitParser()),
                AtLeastParser(1, LetterParser()),
                AtLeastParser(1, DigitParser()),
                AtLeastParser(1, LetterParser())
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
        expect(output).parsedCorrectly(expected)
    }

    @Test
    fun testSequenceCannotGetNext() {
        var input: String
        var output: Pair<Node, ParserContext>?
        var expected: String
        val underTest = SequenceParser(
                AtLeastParser(1, DigitParser()),
                AtLeastParser(1, LetterParser()),
                AtLeastParser(1, DigitParser()),
                AtLeastParser(1, LetterParser())
        )

        input = "1234 asdf 12345"
        output = underTest.test(input, true)
        expect(output).parsedIncorrectly()
    }

    @Test
    fun testNamedSequenceNode() {
        var input: String
        var output: Pair<Node, ParserContext>?
        var expected: String
        val underTest = SequenceParser(
                DigitParser(name = "digit1"),
                DigitParser(name = "digit2"),
                DigitParser(name = "digit3"),
                DigitParser(name = "digit4"),
                name = "sequence"
        )

        input = "1234"
        output = underTest.test(input)
        expected = """
            (SequenceNode:sequence:
              (CharNode:digit1: '1')
              (CharNode:digit2: '2')
              (CharNode:digit3: '3')
              (CharNode:digit4: '4')
            )
        """
        expect(output).parsedCorrectly(expected)
    }

}
