package com.copperleaf.kudzu.parser.many

import com.copperleaf.kudzu.containsExactly
import com.copperleaf.kudzu.expectThat
import com.copperleaf.kudzu.get
import com.copperleaf.kudzu.isEqualTo
import com.copperleaf.kudzu.isFalse
import com.copperleaf.kudzu.isNonTerminal
import com.copperleaf.kudzu.isNotNull
import com.copperleaf.kudzu.isTrue
import com.copperleaf.kudzu.node
import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.parsedCorrectly
import com.copperleaf.kudzu.parsedIncorrectly
import com.copperleaf.kudzu.parser.ParserContext
import com.copperleaf.kudzu.parser.ParserResult
import com.copperleaf.kudzu.parser.chars.AnyCharParser
import com.copperleaf.kudzu.parser.chars.CharInParser
import com.copperleaf.kudzu.parser.chars.DigitParser
import com.copperleaf.kudzu.parser.chars.LetterParser
import com.copperleaf.kudzu.parser.text.AnyTokenParser
import com.copperleaf.kudzu.test
import com.copperleaf.kudzu.withChildren
import io.kotest.core.spec.style.StringSpec

class TestManys : StringSpec({

    "testManyCharParser" {
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
        expectThat(underTest.predict(ParserContext.fromString(input))).isTrue()

        expectThat(output).parsedCorrectly(expected)
            .node()
            .isNonTerminal()
            .get { text }
            .isEqualTo("asdf")
        expectThat(underTest.predict(ParserContext.fromString(input))).isTrue()

        expectThat(underTest.test("")).parsedCorrectly()
        expectThat(underTest.predict(ParserContext.fromString(""))).isFalse()
    }

    "testAtLeastCharParser" {
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
        expectThat(underTest.predict(ParserContext.fromString(input))).isTrue()

        input = "asd"
        output = underTest.test(input)
        expectThat(output).parsedIncorrectly()
        expectThat(underTest.predict(ParserContext.fromString(input))).isTrue()

        expectThat(underTest.test("")).parsedIncorrectly()
        expectThat(underTest.predict(ParserContext.fromString(""))).isFalse()
    }

    "testAtMostCharParser" {
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
        expectThat(underTest.predict(ParserContext.fromString(input))).isTrue()

        input = "qwerty"
        output = underTest.test(input)
        expectThat(output).parsedIncorrectly()
        expectThat(underTest.predict(ParserContext.fromString(input))).isTrue()

        input = "as1234"
        output = underTest.test(input)
        expectThat(output).parsedIncorrectly()
        expectThat(underTest.predict(ParserContext.fromString(input))).isTrue()

        input = "12asdf"
        output = underTest.test(input)
        expectThat(output).parsedIncorrectly()
        expectThat(underTest.predict(ParserContext.fromString(input))).isFalse()

        expectThat(underTest.test("")).parsedCorrectly()
        expectThat(underTest.predict(ParserContext.fromString(""))).isFalse()
    }

    "testManyDigitsParser" {
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
        expectThat(underTest.predict(ParserContext.fromString(input))).isTrue()

        input = "1234 asdf"
        output = underTest.test(input)
        expectThat(output).parsedIncorrectly()
        expectThat(underTest.predict(ParserContext.fromString(input))).isTrue()

        input = "asdf"
        output = underTest.test(input)
        expectThat(output).parsedIncorrectly()
        expectThat(underTest.predict(ParserContext.fromString(input))).isFalse()

        expectThat(underTest.test("")).parsedCorrectly()
        expectThat(underTest.predict(ParserContext.fromString(""))).isFalse()
    }

    "testAtLeastDigitsParser" {
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
        expectThat(underTest.predict(ParserContext.fromString(input))).isTrue()

        input = "1234 asdf"
        output = underTest.test(input)
        expectThat(output).parsedIncorrectly()
        expectThat(underTest.predict(ParserContext.fromString(input))).isTrue()

        input = "123 asdf"
        output = underTest.test(input)
        expectThat(output).parsedIncorrectly()
        expectThat(underTest.predict(ParserContext.fromString(input))).isTrue()

        expectThat(underTest.test("")).parsedIncorrectly()
        expectThat(underTest.predict(ParserContext.fromString(""))).isFalse()
    }

    "testAtMostDigitsParser" {
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
        expectThat(underTest.predict(ParserContext.fromString(input))).isTrue()

        input = "1234 asdf"
        output = underTest.test(input)
        expectThat(output).parsedIncorrectly()
        expectThat(underTest.predict(ParserContext.fromString(input))).isTrue()

        input = "12345 asdf"
        output = underTest.test(input)
        expectThat(output).parsedIncorrectly()
        expectThat(underTest.predict(ParserContext.fromString(input))).isTrue()

        expectThat(underTest.test("")).parsedCorrectly()
        expectThat(underTest.predict(ParserContext.fromString(""))).isFalse()
    }

    "testTimesParser" {
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
        expectThat(underTest.predict(ParserContext.fromString(input))).isTrue()

        input = "12345"
        output = underTest.test(input)
        expectThat(output).parsedIncorrectly()
        expectThat(underTest.predict(ParserContext.fromString(input))).isTrue()

        input = "123"
        output = underTest.test(input)
        expectThat(output).parsedIncorrectly()
        expectThat(underTest.predict(ParserContext.fromString(input))).isTrue()

        expectThat(underTest.test("")).parsedIncorrectly()
        expectThat(underTest.predict(ParserContext.fromString(""))).isFalse()
    }

    "testBetweenTimesParser" {
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
        expectThat(underTest.predict(ParserContext.fromString(input))).isTrue()

        input = "1"
        output = underTest.test(input)
        expectThat(output).parsedIncorrectly()
        expectThat(underTest.predict(ParserContext.fromString(input))).isTrue()

        input = "12"
        output = underTest.test(input)
        expectThat(output).parsedCorrectly()
        expectThat(underTest.predict(ParserContext.fromString(input))).isTrue()

        input = "123"
        output = underTest.test(input)
        expectThat(output).parsedCorrectly()
        expectThat(underTest.predict(ParserContext.fromString(input))).isTrue()

        input = "1234"
        output = underTest.test(input)
        expectThat(output).parsedCorrectly()
        expectThat(underTest.predict(ParserContext.fromString(input))).isTrue()

        input = "12345"
        output = underTest.test(input)
        expectThat(output).parsedIncorrectly()
        expectThat(underTest.predict(ParserContext.fromString(input))).isTrue()

        expectThat(underTest.test("")).parsedIncorrectly()
        expectThat(underTest.predict(ParserContext.fromString(""))).isFalse()
    }

    "testUntilParser" {
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
        expectThat(underTest.predict(ParserContext.fromString(input))).isTrue()

        expectThat(underTest.test("")).parsedCorrectly()
        expectThat(underTest.predict(ParserContext.fromString(""))).isFalse()
    }

    "testNamedManyNode" {
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
        expectThat(underTest.predict(ParserContext.fromString(input))).isTrue()

        expectThat(underTest.test("")).parsedCorrectly()
        expectThat(underTest.predict(ParserContext.fromString(""))).isFalse()
    }

    "testSeparatedByParser" {
        val underTest = SeparatedByParser(
            term = AnyTokenParser(),
            separator = CharInParser(','),
        )

        val output = underTest.parse(ParserContext.fromString("asdf,1234,asd123"))
        expectThat(output)
            .parsedCorrectly()
            .node()
            .isNotNull()
            .apply {
                nodeList.map { it.text }.containsExactly("asdf", "1234", "asd123")
            }
    }
})
