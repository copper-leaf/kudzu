package com.copperleaf.kudzu.parser

import com.copperleaf.kudzu.Node
import com.copperleaf.kudzu.ParserContext
import com.copperleaf.kudzu.checkParsingWhenEmpty
import com.copperleaf.kudzu.isNonTerminal
import com.copperleaf.kudzu.node
import com.copperleaf.kudzu.parsedCorrectly
import com.copperleaf.kudzu.parsedIncorrectly
import com.copperleaf.kudzu.withChildren
import kotlin.test.Test
import strikt.api.expectThat
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
        expectThat(output).parsedCorrectly(expected)
            .node()
            .isNonTerminal()
            .withChildren(4)
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()

        expectThat(output).parsedCorrectly(expected)
            .node()
            .isNonTerminal()
            .get { text }
            .isEqualTo("asdf")
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()

        underTest.checkParsingWhenEmpty()
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
        expectThat(output).parsedCorrectly(expected)
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = "asd"
        output = underTest.test(input)
        expectThat(output).parsedIncorrectly()
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()

        underTest.checkParsingWhenEmpty()
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
        expectThat(output).parsedCorrectly(expected)
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = "qwerty"
        output = underTest.test(input)
        expectThat(output).parsedIncorrectly()
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = "as1234"
        output = underTest.test(input)
        expectThat(output).parsedIncorrectly()
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = "12asdf"
        output = underTest.test(input)
        expectThat(output).parsedIncorrectly()
        expectThat(underTest.predict(ParserContext(input, 0, false))).isFalse()

        underTest.checkParsingWhenEmpty()
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
        expectThat(output).parsedCorrectly(expected)
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = "1234 asdf"
        output = underTest.test(input)
        expectThat(output).parsedIncorrectly()
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = "asdf"
        output = underTest.test(input)
        expectThat(output).parsedIncorrectly()
        expectThat(underTest.predict(ParserContext(input, 0, false))).isFalse()

        underTest.checkParsingWhenEmpty()
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
        expectThat(output).parsedCorrectly(expected)
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = "1234 asdf"
        output = underTest.test(input)
        expectThat(output).parsedIncorrectly()
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = "123 asdf"
        output = underTest.test(input)
        expectThat(output).parsedIncorrectly()
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()

        underTest.checkParsingWhenEmpty()
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
        expectThat(output).parsedCorrectly(expected)
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = "1234 asdf"
        output = underTest.test(input)
        expectThat(output).parsedIncorrectly()
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = "12345 asdf"
        output = underTest.test(input)
        expectThat(output).parsedIncorrectly()
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()

        underTest.checkParsingWhenEmpty()
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
        expectThat(output).parsedCorrectly(expected)
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = "12345"
        output = underTest.test(input)
        expectThat(output).parsedIncorrectly()
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = "123"
        output = underTest.test(input)
        expectThat(output).parsedIncorrectly()
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()

        underTest.checkParsingWhenEmpty()
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
        expectThat(output).parsedCorrectly(expected)
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = "1"
        output = underTest.test(input)
        expectThat(output).parsedIncorrectly()
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = "12"
        output = underTest.test(input)
        expectThat(output).parsedCorrectly()
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = "123"
        output = underTest.test(input)
        expectThat(output).parsedCorrectly()
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = "1234"
        output = underTest.test(input)
        expectThat(output).parsedCorrectly()
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = "12345"
        output = underTest.test(input)
        expectThat(output).parsedIncorrectly()
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()

        underTest.checkParsingWhenEmpty()
    }

    @Test
    fun testUntilParser() {
        var input: String
        var output: Pair<Node, ParserContext>?
        var expected: String
        val underTest = UntilParser(LetterParser(), DigitParser())

        input = "asdf1234"
        output = underTest.test(input)
        expected = """
            (ManyNode:
              (CharNode: 'a')
              (CharNode: 's')
              (CharNode: 'd')
              (CharNode: 'f')
            )
        """
        expectThat(output).parsedCorrectly(expected, allowRemaining = true)
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()

        underTest.checkParsingWhenEmpty()
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
        expectThat(output).parsedCorrectly(expected)
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()

        underTest.checkParsingWhenEmpty()
    }
}
