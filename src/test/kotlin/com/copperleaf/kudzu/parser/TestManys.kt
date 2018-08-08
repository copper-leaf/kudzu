package com.copperleaf.kudzu.parser

import com.copperleaf.kudzu.Node
import com.copperleaf.kudzu.ParserContext
import com.copperleaf.kudzu.isNonTerminal
import com.copperleaf.kudzu.node
import com.copperleaf.kudzu.parsedCorrectly
import com.copperleaf.kudzu.parsedIncorrectly
import com.copperleaf.kudzu.withChildren
import org.junit.jupiter.api.Test
import strikt.api.expect
import strikt.assertions.isEqualTo
import strikt.assertions.isFalse
import strikt.assertions.isTrue

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
                .node()
                .isNonTerminal()
                .withChildren(4)
        expect(underTest.predict(ParserContext(input, 0, false))).isTrue()

        expect(output).parsedCorrectly(expected)
                .node()
                .isNonTerminal()
                .map { text }
                .isEqualTo("asdf")
        expect(underTest.predict(ParserContext(input, 0, false))).isTrue()
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
        expect(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = "asd"
        output = underTest.test(input)
        expect(output).parsedIncorrectly()
        expect(underTest.predict(ParserContext(input, 0, false))).isTrue()
    }

    @Test
    fun testAtMostCharParser() {
        var input: String
        var output: Pair<Node, ParserContext>?
        var expected: String
        val underTest = AtMostParser(4, LetterParser())

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
        expect(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = "qwerty"
        output = underTest.test(input)
        expect(output).parsedIncorrectly()
        expect(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = "as1234"
        output = underTest.test(input)
        expect(output).parsedIncorrectly()
        expect(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = "12asdf"
        output = underTest.test(input)
        expect(output).parsedIncorrectly()
        expect(underTest.predict(ParserContext(input, 0, false))).isFalse()
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
        expect(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = "1234 asdf"
        output = underTest.test(input)
        expect(output).parsedIncorrectly()
        expect(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = "asdf"
        output = underTest.test(input)
        expect(output).parsedIncorrectly()
        expect(underTest.predict(ParserContext(input, 0, false))).isFalse()
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
        expect(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = "1234 asdf"
        output = underTest.test(input)
        expect(output).parsedIncorrectly()
        expect(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = "123 asdf"
        output = underTest.test(input)
        expect(output).parsedIncorrectly()
        expect(underTest.predict(ParserContext(input, 0, false))).isTrue()
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
        expect(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = "1234 asdf"
        output = underTest.test(input)
        expect(output).parsedIncorrectly()
        expect(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = "12345 asdf"
        output = underTest.test(input)
        expect(output).parsedIncorrectly()
        expect(underTest.predict(ParserContext(input, 0, false))).isTrue()
    }

    @Test
    fun testTimesParser() {
        var input: String
        var output: Pair<Node, ParserContext>?
        var expected: String
        val underTest = TimesParser(4, DigitParser())

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
        expect(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = "12345"
        output = underTest.test(input)
        expect(output).parsedIncorrectly()
        expect(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = "123"
        output = underTest.test(input)
        expect(output).parsedIncorrectly()
        expect(underTest.predict(ParserContext(input, 0, false))).isTrue()
    }

    @Test
    fun testBetweenTimesParser() {
        var input: String
        var output: Pair<Node, ParserContext>?
        var expected: String
        val underTest = BetweenTimesParser(2, 4, DigitParser())

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
        expect(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = "1"
        output = underTest.test(input)
        expect(output).parsedIncorrectly()
        expect(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = "12"
        output = underTest.test(input)
        expect(output).parsedCorrectly()
        expect(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = "123"
        output = underTest.test(input)
        expect(output).parsedCorrectly()
        expect(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = "1234"
        output = underTest.test(input)
        expect(output).parsedCorrectly()
        expect(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = "12345"
        output = underTest.test(input)
        expect(output).parsedIncorrectly()
        expect(underTest.predict(ParserContext(input, 0, false))).isTrue()
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
        expect(underTest.predict(ParserContext(input, 0, false))).isTrue()
    }

}