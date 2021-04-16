package com.copperleaf.kudzu.parser.many

import com.copperleaf.kudzu.*
import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.parser.ParserContext
import com.copperleaf.kudzu.parser.ParserResult
import com.copperleaf.kudzu.parser.chars.AnyCharParser
import com.copperleaf.kudzu.parser.chars.DigitParser
import com.copperleaf.kudzu.parser.chars.LetterParser
import kotlin.test.Test

class TestManys {

    @Test
    fun testManyCharParser() {
        var input: String
        var output: ParserResult<Node>?
        var expected: String
        val underTest = ManyParser(AnyCharParser())

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
        var output: ParserResult<Node>?
        var expected: String
        val underTest = AtLeastParser(AnyCharParser(), minSize = 4)

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
        var output: ParserResult<Node>?
        var expected: String
        val underTest = AtMostParser(LetterParser(), maxSize = 4)

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
        var output: ParserResult<Node>?
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
        var output: ParserResult<Node>?
        var expected: String
        val underTest = AtLeastParser(DigitParser(), minSize = 4)

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
        var output: ParserResult<Node>?
        var expected: String
        val underTest = AtMostParser(DigitParser(), maxSize = 4)

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
        var output: ParserResult<Node>?
        var expected: String
        val underTest = TimesParser(DigitParser(), times = 4)

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
        var output: ParserResult<Node>?
        var expected: String
        val underTest = BetweenTimesParser(DigitParser(), minSize = 2, maxSize = 4)

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
        var output: ParserResult<Node>?
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
        var output: ParserResult<Node>?
        var expected: String
        val underTest = ManyParser(
            DigitParser(),
        )

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

        underTest.checkParsingWhenEmpty()
    }
}
