package com.eden.monadik

import com.eden.monadik.parser.AtLeastParser
import com.eden.monadik.parser.AtMostParser
import com.eden.monadik.parser.CharParser
import com.eden.monadik.parser.DigitParser
import com.eden.monadik.parser.ManyParser
import org.junit.jupiter.api.Test
import strikt.api.expect
import strikt.assertions.hasSize
import strikt.assertions.isNotNull

class TestManys {

    @Test
    fun testManyCharParser() {
        var input: String
        var output: Pair<Node, ParserContext>?
        var expected: String
        val underTest = ManyParser(CharParser())

        input = "asdf"
        output = underTest.test(input)
        expected = """
            (ManyNode:
              (CharNode: 'a')
              (CharNode: 's')
              (CharNode: 'd')
              (CharNode: 'f')
            )
        """
        expect(output).parsedCorrectly(expected)
        expect(output!!.first.children).isNotNull().hasSize(4)
    }

    @Test
    fun testAtLeastCharParser() {
        var input: String
        var output: Pair<Node, ParserContext>?
        var expected: String
        val underTest = AtLeastParser(4, CharParser())

        input = "asdf"
        output = underTest.test(input)
        expected = """
            (ManyNode:
              (CharNode: 'a')
              (CharNode: 's')
              (CharNode: 'd')
              (CharNode: 'f')
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
        var output: Pair<Node, ParserContext>?
        var expected: String
        val underTest = AtMostParser(4, CharParser())

        input = "asdf"
        output = underTest.test(input)
        expected = """
            (ManyNode:
              (CharNode: 'a')
              (CharNode: 's')
              (CharNode: 'd')
              (CharNode: 'f')
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
        var output: Pair<Node, ParserContext>?
        var expected: String
        val underTest = ManyParser(DigitParser())

        input = "1234"
        output = underTest.test(input)
        expected = """
            (ManyNode:
              (CharNode: '1')
              (CharNode: '2')
              (CharNode: '3')
              (CharNode: '4')
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
        var output: Pair<Node, ParserContext>?
        var expected: String
        val underTest = AtLeastParser(4, DigitParser())

        input = "1234"
        output = underTest.test(input)
        expected = """
            (ManyNode:
              (CharNode: '1')
              (CharNode: '2')
              (CharNode: '3')
              (CharNode: '4')
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
        var output: Pair<Node, ParserContext>?
        var expected: String
        val underTest = AtMostParser(4, DigitParser())

        input = "1234"
        output = underTest.test(input)
        expected = """
            (ManyNode:
              (CharNode: '1')
              (CharNode: '2')
              (CharNode: '3')
              (CharNode: '4')
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

    @Test
    fun testNamedManyNode() {
        var input: String
        var output: Pair<Node, ParserContext>?
        var expected: String
        val underTest = ManyParser(
                DigitParser(name = "digit"),
                name = "many"
        )

        input = "1234"
        output = underTest.test(input)
        expected = """
            (ManyNode:many:
              (CharNode:digit: '1')
              (CharNode:digit: '2')
              (CharNode:digit: '3')
              (CharNode:digit: '4')
            )
        """
        expect(output).parsedCorrectly(expected)
    }

}
