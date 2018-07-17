package com.eden.monadik

import org.junit.jupiter.api.Test
import strikt.api.expect

class TestManys {

    @Test
    fun testManyCharParser() {
        var input: String
        var output: Pair<Node, ParsingContext>?
        var expected: String
        val underTest = ManyParser(CharParser())

        input = "asdf"
        output = underTest.test(input)
        expected = """
            (ManyNode:
              (CharNode: a)
              (CharNode: s)
              (CharNode: d)
              (CharNode: f)
            )
        """
        expect(output).parsedCorrectly(expected)
    }

    @Test
    fun testAtLeastCharParser() {
        var input: String
        var output: Pair<Node, ParsingContext>?
        var expected: String
        val underTest = AtLeastParser(4, CharParser())

        input = "asdf"
        output = underTest.test(input)
        expected = """
            (ManyNode:
              (CharNode: a)
              (CharNode: s)
              (CharNode: d)
              (CharNode: f)
            )
        """
        expect(output).parsedCorrectly(expected)

        input = "asd"
        output = underTest.test(input)
        expect(output).parsedIncorrectly()
    }

    @Test
    fun testAtMostCharParser() {
        var input: String
        var output: Pair<Node, ParsingContext>?
        var expected: String
        val underTest = AtMostParser(4, CharParser())

        input = "asdf"
        output = underTest.test(input)
        expected = """
            (ManyNode:
              (CharNode: a)
              (CharNode: s)
              (CharNode: d)
              (CharNode: f)
            )
        """
        expect(output).parsedCorrectly(expected)

        input = "qwerty"
        output = underTest.test(input)
        expect(output).parsedIncorrectly()
    }

    @Test
    fun testManyDigitsParser() {
        var input: String
        var output: Pair<Node, ParsingContext>?
        var expected: String
        val underTest = ManyParser(DigitParser())

        input = "1234"
        output = underTest.test(input)
        expected = """
            (ManyNode:
              (CharNode: 1)
              (CharNode: 2)
              (CharNode: 3)
              (CharNode: 4)
            )
        """
        expect(output).parsedCorrectly(expected)

        input = "1234 asdf"
        output = underTest.test(input)
        expect(output).parsedIncorrectly()
    }

    @Test
    fun testAtLeastDigitsParser() {
        var input: String
        var output: Pair<Node, ParsingContext>?
        var expected: String
        val underTest = AtLeastParser(4, DigitParser())

        input = "1234"
        output = underTest.test(input)
        expected = """
            (ManyNode:
              (CharNode: 1)
              (CharNode: 2)
              (CharNode: 3)
              (CharNode: 4)
            )
        """
        expect(output).parsedCorrectly(expected)

        input = "1234 asdf"
        output = underTest.test(input)
        expect(output).parsedIncorrectly()

        input = "123 asdf"
        output = underTest.test(input)
        expect(output).parsedIncorrectly()
    }

    @Test
    fun testAtMostDigitsParser() {
        var input: String
        var output: Pair<Node, ParsingContext>?
        var expected: String
        val underTest = AtMostParser(4, DigitParser())

        input = "1234"
        output = underTest.test(input)
        expected = """
            (ManyNode:
              (CharNode: 1)
              (CharNode: 2)
              (CharNode: 3)
              (CharNode: 4)
            )
        """
        expect(output).parsedCorrectly(expected)

        input = "1234 asdf"
        output = underTest.test(input)
        expect(output).parsedIncorrectly()

        input = "12345 asdf"
        output = underTest.test(input)
        expect(output).parsedIncorrectly()
    }

}
