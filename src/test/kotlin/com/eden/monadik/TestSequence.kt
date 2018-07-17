package com.eden.monadik

import org.junit.jupiter.api.Test
import strikt.api.expect

class TestSequence {

    @Test
    fun testSequenceOfCharParser() {
        var input: String
        var output: Pair<Node, ParsingContext>?
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
              (CharNode: 1)
              (CharNode: 2)
              (CharNode: 3)
              (CharNode: 4)
            )
        """
        expect(output).parsedCorrectly(expected)
    }

    @Test
    fun testSequenceOfManysParser() {
        var input: String
        var output: Pair<Node, ParsingContext>?
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
                (CharNode: 1)
                (CharNode: 2)
                (CharNode: 3)
                (CharNode: 4)
              )
              (ManyNode:
                (CharNode:  )
              )
              (ManyNode:
                (CharNode: a)
                (CharNode: s)
                (CharNode: d)
                (CharNode: f)
              )
            )
        """
        expect(output).parsedCorrectly(expected)
    }

    @Test
    fun testSequenceOfManysParserSkippingWhitespace() {
        var input: String
        var output: Pair<Node, ParsingContext>?
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
                (CharNode: 1)
                (CharNode: 2)
                (CharNode: 3)
                (CharNode: 4)
              )
              (ManyNode:
                (CharNode: a)
                (CharNode: s)
                (CharNode: d)
                (CharNode: f)
              )
              (ManyNode:
                (CharNode: 1)
                (CharNode: 2)
                (CharNode: 3)
                (CharNode: 4)
                (CharNode: 5)
              )
              (ManyNode:
                (CharNode: q)
                (CharNode: w)
                (CharNode: e)
                (CharNode: r)
                (CharNode: t)
                (CharNode: y)
              )
            )
        """
        expect(output).parsedCorrectly(expected)
    }

    @Test
    fun testSequenceCannotGetNext() {
        var input: String
        var output: Pair<Node, ParsingContext>?
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

}
